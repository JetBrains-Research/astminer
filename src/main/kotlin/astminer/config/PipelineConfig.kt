package astminer.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

/**
 * Config which defines the pipeline
 * @see astminer.pipeline.Pipeline
 */
@Serializable
data class PipelineConfig(
    val inputDir: String,
    val outputDir: String,
    val parser: ParserConfig,
    val filters: List<FilterConfig> = emptyList(),
    @SerialName("label") val labelExtractor: LabelExtractorConfig,
    val storage: StorageConfig,
    val numOfThreads: Int = 1
) {
    init {
        if (numOfThreads <= 0) {
            throw SerializationException("Number of threads must be a positive integer")
        }
    }
}
