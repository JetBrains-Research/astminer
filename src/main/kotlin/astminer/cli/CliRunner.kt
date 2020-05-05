package astminer.cli

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
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long

var task: String = ""

class CliRunner: CliktCommand() {

    /**
     * @param parser class that implements parsing
     * @param extension file extension to choose files for parsing
     */
    data class SupportedLanguage(val parser: Parser<out Node>, val extension: String)

    /**
     * @param astStorage class that implements ast's storage
     * @param type name of storage
     */
    data class SupportedAstStorage(val astStorage: AstStorage, val type: String)

    /**
     * List of supported language extensions and corresponding parsers.
     */
    val supportedLanguages = listOf(
        SupportedLanguage(GumTreeJavaParser(), "java"),
        SupportedLanguage(FuzzyCppParser(), "c"),
        SupportedLanguage(FuzzyCppParser(), "cpp"),
        SupportedLanguage(PythonParser(), "py")
    )

    val supportedAstStorages = listOf(
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

    val outputDir: String by option(
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

    val batchMode: Boolean by option(
        "--batchMode",
        help = "Store path contexts in batches of `batchSize` to reduce memory usage. " +
                "If passed, limits on tokens and paths will be ignored!"
    ).flag(default = false)

    val batchSize: Long by option(
        "--batchSize",
        help = "Number of path contexts stored in each batch. Should only be used with `batchMode` flag."
    ).long().default(100)

    fun parseArgs(args: Array<String>) {
        if (args[0] == "preprocess" || args[0] == "parse" || args[0] == "pathContexts" || args[0] == "code2vec") {
            task = args[0]
        } else {
            throw Exception("The first argument should be task's name: either 'preprocess', 'parse', 'pathContexts', or 'code2vec'")
        }
        CliRunner().main(args.sliceArray(1 until args.size))
    }

    override fun run() {
        when (task) {
            "preprocess" -> ProjectPreprocessor().preprocessing()
            "parse" -> ProjectParser().parsing()
            "pathContexts" -> PathContextsExtractor().extractPathContexts()
            "code2vec" -> return //TODO
        }
    }
}