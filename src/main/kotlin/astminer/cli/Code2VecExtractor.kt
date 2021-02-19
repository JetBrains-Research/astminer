package astminer.cli

import astminer.common.getProjectFilesWithExtension
import astminer.common.getNormalizedToken
import astminer.common.model.LabeledPathContexts
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.paths.Code2VecPathStorage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import java.io.File

class Code2VecExtractor(private val customLabelExtractor: LabelExtractor? = null) : CliktCommand() {

    private val supportedLanguages = listOf("java", "c", "cpp", "py", "js")

    val extensions: List<String> by option(
        "--lang",
        help = "Comma-separated list of file extensions that will be parsed.\n" +
                "Supports 'c', 'cpp', 'java', 'py', 'js', defaults to all these extensions."
    ).split(",").default(supportedLanguages)

    val projectRoot: String by option(
        "--project",
        help = "Path to the project that will be parsed"
    ).required()

    val outputDirName: String by option(
        "--output",
        help = "Path to directory where the output will be stored"
    ).required()

    val maxPathLength: Int by option(
        "--maxL",
        help = "Maximum length of path for code2vec"
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
        help = "if passed with file-level granularity, the folder name is used to label paths"
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

    private fun <T : Node> extractFromTree(
        parseResult: ParseResult<out T>,
        miner: PathMiner,
        storage: Code2VecPathStorage,
        labelExtractor: LabelExtractor
    ) {
        val labeledParseResults = labelExtractor.toLabeledData(parseResult)

        // Retrieve paths from every node individually
        labeledParseResults.forEach { (root, label) ->
            val paths = miner.retrievePaths(root).take(maxPathContexts)
            storage.store(LabeledPathContexts(label, paths.map {
                toPathContext(it) { node ->
                    node.getNormalizedToken()
                }
            }))
        }
    }

    private fun extract(labelExtractor: LabelExtractor) {
        val outputDir = File(outputDirName)
        for (extension in extensions) {
            val miner = PathMiner(PathRetrievalSettings(maxPathLength, maxPathWidth))

            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            // Choose type of storage
            val storage = Code2VecPathStorage(outputDirForLanguage.path, maxPaths, maxTokens)
            // Choose type of parser
            val parser = getParser(
                extension,
                javaParser
            )
            // Parse project one file at a time
            parser.parseFiles(getProjectFilesWithExtension(File(projectRoot), extension)) {
                normalizeParseResult(it, isTokenSplitted)
                // Retrieve labeled data
                extractFromTree(it, miner, storage, labelExtractor)
            }
            // Save stored data on disk
            storage.close()
        }
    }

    override fun run() {
        val labelExtractor = customLabelExtractor ?: getLabelExtractor(
            granularityLevel,
            javaParser,
            isMethodNameHide,
            excludeModifiers,
            excludeAnnotations,
            filterConstructors,
            maxMethodNameLength,
            maxTokenLength,
            maxTreeSize,
            folderLabel
        )
        extract(labelExtractor)
    }
}