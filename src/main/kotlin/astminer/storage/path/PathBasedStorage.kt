package astminer.storage.path

import astminer.common.model.*
import astminer.common.model.LabeledResult
import astminer.common.model.Storage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import java.io.File
import java.io.PrintWriter

/**
 * Config for CountingPathStorage which contains all hyperparameters for path extraction.
 * @property maxPathLength The maximum length of a single path (based on the formal math definition of path length)
 * @property maxPathWidth The maximum width of a single path (based on the formal math definition of path width)
 * @property maxTokens The maximum number of tokens saved per extraction
 * @property maxPaths The maximum number of paths saved per extraction
 * @property maxPathContextsPerEntity The maximum number of path contexts that should be extracted from tree.
 * In other words, the maximum number of path contexts to save from each file/method (depending on granularity)
 */
data class PathBasedStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null
)

/**
 * Base class for all path storages. Extracts paths from given LabellingResult and stores it in a specified format.
 * @property outputDirectoryPath The path to the output directory.
 * @property config The config that contains hyperparameters for path extraction.
 */
abstract class PathBasedStorage(
    final override val outputDirectoryPath: String,
    private val config: PathBasedStorageConfig,
) : Storage {

    private val pathMiner = PathMiner(PathRetrievalSettings(config.maxPathLength, config.maxPathWidth))
    private val datasetFileWriters = mutableMapOf<DatasetHoldout, PrintWriter>()

    init {
        File(outputDirectoryPath).mkdirs()
    }

    private fun retrievePaths(node: Node) = if (config.maxPathContextsPerEntity != null) {
        pathMiner.retrievePaths(node).shuffled().take(config.maxPathContextsPerEntity)
    } else {
        pathMiner.retrievePaths(node)
    }

    private fun retrieveLabeledPathContexts(labeledResult: LabeledResult<out Node>): LabeledPathContexts<String> {
        val paths = retrievePaths(labeledResult.root)
        return LabeledPathContexts(
            labeledResult.label,
            paths.map { astPath ->
                toPathContext(astPath) { it.token.replace("\n", "\\n") }
            }
        )
    }

    abstract fun labeledPathContextsToString(labeledPathContexts: LabeledPathContexts<String>): String

    /**
     * Extract paths from [labeledResult] and store them in the specified format.
     */
    override fun store(labeledResult: LabeledResult<out Node>, holdout: DatasetHoldout) {
        val labeledPathContexts = retrieveLabeledPathContexts(labeledResult)
        val output = labeledPathContextsToString(labeledPathContexts)
        val writer = datasetFileWriters.getOrPut(holdout) { holdout.resolveWriter() }
        writer.println(output)
    }

    override fun close() {
        datasetFileWriters.values.map { it.close() }
    }

    private fun DatasetHoldout.resolveWriter(): PrintWriter {
        val holdoutDir = File(outputDirectoryPath).resolve(this.dirName)
        holdoutDir.mkdirs()
        val pathContextFile = holdoutDir.resolve("path_contexts.c2s")
        pathContextFile.createNewFile()
        return PrintWriter(pathContextFile)
    }
}
