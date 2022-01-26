package astminer.storage.path

import astminer.common.model.*
import astminer.common.model.LabeledResult
import astminer.common.model.Storage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
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
 * @property pathExtractionConfig The config that contains hyperparameters for path extraction.
 * @property metaDataConfig The config that contains parameters for saving metadata
 * (for example enabling filepath storage)
 */
abstract class PathBasedStorage(
    final override val outputDirectoryPath: String,
    private val pathExtractionConfig: PathBasedStorageConfig,
    private val metaDataConfig: MetaDataConfig = MetaDataConfig()
) : Storage {

    private val pathMiner = PathMiner(
        PathRetrievalSettings(
            pathExtractionConfig.maxPathLength,
            pathExtractionConfig.maxPathWidth
        )
    )
    private val datasetFileWriters = mutableMapOf<DatasetHoldout, PrintWriter>()
    private val metadataWriters = mutableMapOf<DatasetHoldout, PrintWriter>()

    init {
        File(outputDirectoryPath).mkdirs()
    }

    private fun retrievePaths(node: Node) = if (pathExtractionConfig.maxPathContextsPerEntity != null) {
        pathMiner.retrievePaths(node).shuffled().take(pathExtractionConfig.maxPathContextsPerEntity)
    } else {
        pathMiner.retrievePaths(node)
    }

    private fun retrieveLabeledPathContexts(labeledResult: LabeledResult<out Node>): LabeledPathContexts<String> {
        val paths = retrievePaths(labeledResult.root)
        return LabeledPathContexts(
            labeledResult.label,
            paths.map { astPath ->
                toPathContext(astPath) { it.token.final().replace("\n", "\\n") }
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
        val writer = datasetFileWriters.getOrPut(holdout) { holdout.resolveDataWriter() }
        if (metaDataConfig.metadataRequested) {
            val metaWriter = metadataWriters.getOrPut(holdout) { holdout.resolveMetaWriter() }
            writeMetadata(labeledResult, metaWriter)
        }
        writer.println(output)
    }

    private fun writeMetadata(labeledResult: LabeledResult<out Node>, writer: PrintWriter) {
        val metadata = buildJsonObject {
            put("label", labeledResult.label)
            if (metaDataConfig.storeRanges) put("range", Json.encodeToJsonElement(labeledResult.root.range))
            if (metaDataConfig.storePaths) put("path", labeledResult.filePath)
        }
        writer.println(Json.encodeToString(metadata))
    }

    override fun close() {
        datasetFileWriters.values.map { it.close() }
        metadataWriters.values.map { it.close() }
    }

    private fun DatasetHoldout.resolveWriter(outputFile: String): PrintWriter {
        val holdoutDir = File(outputDirectoryPath).resolve(this.dirName)
        holdoutDir.mkdirs()
        val newOutputFile = holdoutDir.resolve(outputFile)
        newOutputFile.createNewFile()
        return PrintWriter(newOutputFile.outputStream(), true)
    }

    private fun DatasetHoldout.resolveDataWriter() = resolveWriter("path_contexts.c2s")
    private fun DatasetHoldout.resolveMetaWriter() = resolveWriter("metadata.jsonl")
}
