package astminer.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Base class for pipeline configs
 */
@Serializable
sealed class PipelineConfig {
    abstract val inputDir: String
    abstract val outputDir: String
    abstract val parser: ParserConfig
    abstract val storage: StorageConfig
}

/**
 * Pipeline config for pipeline with file-level granularity.
 * In other words, [filters] are used to filter parsed files
 * and [problem] processes and extracts label from parsed files.
 */
@Serializable
@SerialName("file granularity")
data class FilePipelineConfig(
    override val inputDir: String,
    override val outputDir: String,
    override val parser: ParserConfig,
    val filters: List<FileFilterConfig> = emptyList(),
    val problem: FileProblemConfig,
    override val storage: StorageConfig
) : PipelineConfig()

/**
 * Pipeline config for pipeline with function-level granularity.
 * In other words, [filters] are used to test functions
 * and [problem] processes and extracts labels from functions
 */
@Serializable
@SerialName("function granularity")
data class FunctionPipelineConfig(
    override val inputDir: String,
    override val outputDir: String,
    override val parser: ParserConfig,
    val filters: List<FunctionFilterConfig> = emptyList(),
    val problem: FunctionProblemConfig,
    override val storage: StorageConfig
) : PipelineConfig()
