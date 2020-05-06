package astminer.cli

import astminer.ast.CsvAstStorage
import astminer.ast.DotAstStorage
import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.common.preOrder
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.java.GumTreeJavaParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import java.io.File


class ProjectParser : CliktCommand() {

    /**
     * @param parser class that implements parsing
     * @param extension file extension to choose files for parsing
     */
    private data class SupportedLanguage(val parser: Parser<out Node>, val extension: String)

    /**
     * @param astStorage class that implements ast's storage
     * @param type name of storage
     */
    private data class SupportedAstStorage(val astStorage: AstStorage, val type: String)

    /**
     * List of supported language extensions and corresponding parsers.
     */
    private val supportedLanguages = listOf(
            SupportedLanguage(GumTreeJavaParser(), "java"),
            SupportedLanguage(FuzzyCppParser(), "c"),
            SupportedLanguage(FuzzyCppParser(), "cpp"),
            SupportedLanguage(PythonParser(), "py")
    )

    private val supportedAstStorages = listOf(
            SupportedAstStorage(CsvAstStorage(), "csv"),
            SupportedAstStorage(DotAstStorage(), "dot")
    )

    val extensions: List<String> by option(
            "--lang",
            help = "Comma-separated list of file extensions that will be parsed.\n" +
                    "Supports 'c', 'cpp', 'java', 'py', defaults to all these extensions."
    ).split(",").default(supportedLanguages.map { it.extension })

    val projectRoot: String by option(
            "--project",
            help = "Path to the project that will be parsed"
    ).required()

    val outputDirName: String by option(
            "--output",
            help = "Path to directory where the output will be stored"
    ).required()

    val astStorageType: String by option(
            "--storage",
            help = "AST storage type ('dot' or 'csv', defaults to 'csv')"
    ).default(supportedAstStorages[0].type)

    val granularityLevel: String by option(
            "--granularity",
            help = "Choose level of granularity ('file' or 'method', defaults to 'file')"
    ).default("file")

    val isMethodNameHide: Boolean by option(
            "--hide-method-name",
            help = "if passed with method level granularity, the names of all methods are replaced with placeholder token"
    ).flag(default = false)

    val isTokenSplitted: Boolean by option(
            "--split-tokens",
            help = "if passed, split tokens into sequence of tokens"
    ).flag(default = false)

    val excludeModifiers: List<String> by option(
            "--filter-modifiers",
            help = "Comma-separated list of function's modifiers, which should be filtered." +
                    "Works only for method-level granulation."
    ).split(",").default(emptyList())

    val excludeAnnotations: List<String> by option(
            "--filter-annotations",
            help = "Comma-separated list of function's annotations, which should be filtered." +
                    "Works only for method-level granulation."
    ).split(",").default(emptyList())

    val filterConstructors: Boolean by option(
            "--remove-constructors",
            help = "Remove constructor methods, works for method-level granulation"
    ).flag(default = false)

    val excludeNodes: List<String> by option(
            "--remove-nodes",
            help = "Comma-separated list of node types, which must be removed from asts."
    ).split(",").default(emptyList())

    val javaParser: String by option(
            "--java-parser",
            help = "Choose a parser for .java files." +
                    "'gumtree' for GumTree parser, 'antlr' for antlr parser."
    ).default("gumtree")

    val maxMethodNameLength: Int by option(
            "--max-method-name-length",
            help = "Filtering methods with a large sequence of subtokens in their names"
    ).int().default(-1)

    val maxTokenLength: Int by option(
            "--max-token-length",
            help = "Filter methods containing a long sequence of subtokens in the ast node"
    ).int().default(-1)

    val maxTreeSize: Int by option(
            "--max-tree-size",
            help = "Filter methods by their ast size"
    ).int().default(-1)

    private fun getParser(extension: String): Parser<out Node> {
        return when (extension) {
            "java" -> {
                when (javaParser) {
                    "gumtree" -> GumTreeJavaParser()
                    "antlr" -> JavaParser()
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser for java extension $javaParser")
                    }
                }
            }
            else -> {
                supportedLanguages.find { it.extension == extension }?.parser
                        ?: throw UnsupportedOperationException("Unsupported extension $extension")
            }
        }
    }

    private fun getStorage(storageType: String): AstStorage {
        for (storage in supportedAstStorages) {
            if (storageType == storage.type) {
                return storage.astStorage
            }
        }
        throw UnsupportedOperationException("Unsupported AST storage $storageType")
    }

    private fun getGranularity(granularityLevel: String): Granularity {
        when (granularityLevel) {
            "file" -> return FileGranularity(isTokenSplitted)
            "method" -> {
                val filterPredicates = mutableListOf(
                        ModifierFilterPredicate(excludeModifiers), AnnotationFilterPredicate(excludeAnnotations),
                        MethodNameLengthFilterPredicate(maxMethodNameLength), TokenLengthFilterPredicate(maxTokenLength),
                        TreeSizeFilterPredicate(maxTreeSize)
                )
                if (filterConstructors) {
                    filterPredicates.add(ConstructorFilterPredicate())
                }
                return MethodGranularity(isTokenSplitted, isMethodNameHide, filterPredicates, javaParser)
            }
        }
        throw UnsupportedOperationException("Unsupported granularity level $granularityLevel")
    }

    private fun parsing() {
        val outputDir = File(outputDirName)
        for (extension in extensions) {
            // Choose type of storage
            val storage = getStorage(astStorageType)
            // Choose type of parser
            val parser = getParser(extension)
            // Choose granularity level
            val granularity = getGranularity(granularityLevel)
            // Parse project
            val parsedProject = parser.parseWithExtension(File(projectRoot), extension)
            // Split project to required granularity level
            val roots = granularity.splitByGranularityLevel(parsedProject, extension)
            roots.forEach { parseResult ->
                val root = parseResult.root
                val filePath = parseResult.filePath
                root?.preOrder()?.forEach { node ->
                    excludeNodes.forEach { node.removeChildrenOfType(it) }
                }
                root?.apply {
                    // Save AST as it is or process it to extract features / path-based representations
                    storage.store(root, label = filePath)
                }
            }
            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            // Save stored data on disk
            storage.save(outputDirForLanguage.path)
        }

    }

    override fun run() {
        parsing()
    }
}