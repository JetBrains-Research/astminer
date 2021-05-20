package astminer.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PipelineConfig {
    abstract val inputDir: String
    abstract val outputDir: String
    abstract val parser: ParserConfig
    abstract val storage: StorageConfig
}

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

@Serializable
data class ParserConfig(
    val type: String,
    val extensions: List<String>
)

