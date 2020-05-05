package astminer.cli

import astminer.common.getNormalizedToken
import astminer.common.model.*
import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.common.splitToSubtokens
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.java.GumTreeJavaParser
import astminer.parse.java.GumTreeMethodSplitter
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

    private val extensions: List<String> by option(
        "--lang",
        help = "File extensions that will be parsed"
    ).split(",").required()

    private val projectRoot = CliRunner().projectRoot

    private val outputDirName = CliRunner().outputDir

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

    private fun <T : Node> extractFromMethods(
        roots: List<ParseResult<T>>,
        methodSplitter: TreeMethodSplitter<T>,
        miner: PathMiner,
        storage: Code2VecPathStorage
    ) {
        val methods = roots.mapNotNull {
            it.root
        }.flatMap {
            methodSplitter.splitIntoMethods(it)
        }
        methods.forEach { methodInfo ->
            val methodNameNode = methodInfo.method.nameNode ?: return@forEach
            val methodRoot = methodInfo.method.root
            val label = splitToSubtokens(methodNameNode.getToken()).joinToString("|")
            methodRoot.preOrder().forEach { it.setNormalizedToken() }
            methodNameNode.setNormalizedToken("METHOD_NAME")

            // Retrieve paths from every node individually
            val paths = miner.retrievePaths(methodRoot).take(maxPathContexts)
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
            val storage = Code2VecPathStorage(outputDirForLanguage.path, batchMode, batchSize)

            when (extension) {
                "c", "cpp" -> {
                    val parser = FuzzyCppParser()
                    val roots = parser.parseWithExtension(File(projectRoot), extension)
                    extractFromMethods(roots, FuzzyMethodSplitter(), miner, storage)
                }
                "java" -> {
                    val parser = GumTreeJavaParser()
                    val roots = parser.parseWithExtension(File(projectRoot), extension)
                    extractFromMethods(roots, GumTreeMethodSplitter(), miner, storage)
                }
                "py" -> {
                    val parser = PythonParser()
                    val roots = parser.parseWithExtension(File(projectRoot), extension)
                    extractFromMethods(roots, PythonMethodSplitter(), miner, storage)
                }
                else -> throw UnsupportedOperationException("Unsupported extension $extension")
            }

            // Save stored data on disk
            storage.save(maxPaths, maxTokens)
        }
    }

    override fun run() {
        extract()
    }
}