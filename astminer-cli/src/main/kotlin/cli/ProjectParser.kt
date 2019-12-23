package cli

import astminer.ast.CsvAstStorage
import astminer.ast.DotAstStorage
import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.java.GumTreeJavaParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.options.flag
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
     * @param granularity class that implements granularity parsing
     * @param level level of granularity
     */
    private data class SupportedGranularityLevel(val granularity: Granularity, val level: String)

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

    private fun getParser(extension: String): Parser<out Node> {
        for (language in supportedLanguages) {
            if (extension == language.extension) {
                return language.parser
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
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
            "method" -> return MethodGranularity(isTokenSplitted, isMethodNameHide, filterConstructors)
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
            var roots = granularity.splitByGranularityLevel(parsedProject, extension)
            // Granularity levels specific work
            if (granularityLevel == "method") {
                roots = roots
                    .filter { modifiersPredicate(it.root, excludeModifiers) }
                    .filter { annotationsPredicate(it.root, excludeAnnotations) }
            }
            roots.forEach { parseResult ->
                val root = parseResult.root
                val filePath = parseResult.filePath
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