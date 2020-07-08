package astminer.cli

import astminer.ast.CsvAstStorage
import astminer.ast.DotAstStorage
import astminer.common.getNormalizedToken
import astminer.common.model.*
import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.common.splitToSubtokens
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.java.GumTreeJavaParser
import astminer.paths.Code2VecPathStorage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import java.io.File

class Code2VecExtractor : CliktCommand() {

    /**
     * @param parser class that implements parsing
     * @param extension file extension to choose files for parsing
     */
    private data class SupportedLanguage(val parser: Parser<out Node>, val extension: String)

    /**
     * List of supported language extensions and corresponding parsers.
     */
    private val supportedLanguages = listOf(
            SupportedLanguage(GumTreeJavaParser(), "java"),
            SupportedLanguage(FuzzyCppParser(), "c"),
            SupportedLanguage(FuzzyCppParser(), "cpp"),
            SupportedLanguage(PythonParser(), "py")
    )

    val extensions: List<String> by option(
        "--lang",
        help = "File extensions that will be parsed"
    ).split(",").required()

    val projectRoot: String by option(
        "--project",
        help = "Path to the project that will be parsed"
    ).required()

    val outputDirName: String by option(
        "--output",
        help = "Path to directory where the output will be stored"
    ).required()

    val maxPathHeight: Int by option(
        "--maxH",
        help = "Maximum height of path for code2vec"
    ).int().default(8)

    val maxPathWidth: Int by option(
        "--maxW",
        help = "Maximum width of path. " +
                "Note, that here width is the difference between token indices in contrast to the original code2vec."
    ).int().default(3)

    val maxPathContexts: Int by option(
        "--maxContexts",
        help = "Number of path contexts to keep from each method."
    ).int().default(500)

    val maxTokens: Long by option(
        "--maxTokens",
        help = "Keep only contexts with maxTokens most popular tokens."
    ).long().default(Long.MAX_VALUE)

    val maxPaths: Long by option(
        "--maxPaths",
        help = "Keep only contexts with maxTokens most popular paths."
    ).long().default(Long.MAX_VALUE)

    val granularityLevel: String by option(
            "--granularity",
            help = "Choose level of granularity ('file' or 'method', defaults to 'file')"
    ).default("file")

    val folderLabel: Boolean by option(
            "--folder-label",
            help = "if passed with file level granularitythan, label paths with folder names"
    ).flag(default = false)

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

    private fun getStorage(storageType: String, directoryPath: String): AstStorage {
        return when (storageType) {
            "csv" -> CsvAstStorage(directoryPath)
            "dot" -> DotAstStorage(directoryPath)
            else -> {
                throw UnsupportedOperationException("Unsupported AST storage $storageType")
            }
        }
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

    private fun <T: Node> extractFromTrees(
            roots: List<ParseResult<T>>,
            miner: PathMiner,
            storage: Code2VecPathStorage
    ) {
        roots.forEach { parseResult ->
            val root = parseResult.root  ?: return@forEach
            val fullPath = parseResult.filePath.split("/")
            var label = ""
            when (granularityLevel) {
                "method" -> {
                    label = splitToSubtokens(fullPath.last()).joinToString("|")
                }
                "file" -> {
                    val fileName = fullPath.last()
                    val folderName = fullPath.elementAt(fullPath.size - 2)
                    label = if (folderLabel) folderName else fileName
                }
            }

            root.preOrder().forEach { it.setNormalizedToken() }
            if (isMethodNameHide)
                root.setNormalizedToken("METHOD_NAME")

            // Retrieve paths from every node individually
            val paths = miner.retrievePaths(root).take(maxPathContexts)
            storage.store(LabeledPathContexts(label, paths.map {
                toPathContext(it) { node ->
                    node.getNormalizedToken()
                }
            }))
        }
    }

    private fun extract() {
        val outputDir = File(outputDirName)
        for (extension in extensions) {
            val miner = PathMiner(PathRetrievalSettings(maxPathHeight, maxPathWidth))

            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            // Choose type of storage
            val storage = Code2VecPathStorage(outputDirForLanguage.path, maxPaths, maxTokens)
            // Choose type of parser
            val parser = getParser(extension)
            // Choose granularity level
            val granularity = getGranularity(granularityLevel)
            // Parse project
            val parsedProject = parser.parseWithExtension(File(projectRoot), extension)
            // Split project to required granularity level
            val roots = granularity.splitByGranularityLevel(parsedProject, extension) as List<ParseResult<Node>>
            extractFromTrees(roots, miner, storage)
            // Save stored data on disk
            storage.close()
        }
    }

    override fun run() {
        extract()
    }
}