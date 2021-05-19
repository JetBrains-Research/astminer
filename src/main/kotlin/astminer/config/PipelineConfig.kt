package astminer.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PipelineConfig {
    abstract val inputDir: String
    abstract val outputDir: String
    abstract val parserConfig: ParserConfig
    abstract val storageConfig: StorageConfig
}

@Serializable
@SerialName("file granularity")
data class FilePipelineConfig(
    override val inputDir: String,
    override val outputDir: String,
    @SerialName("parser")
    override val parserConfig: ParserConfig,
    @SerialName("filters")
    val filterConfigs: List<FileFilterConfig> = emptyList(),
    @SerialName("problem")
    val problemConfig: FileProblemConfig,
    @SerialName("storage")
    override val storageConfig: StorageConfig
) : PipelineConfig()

@Serializable
@SerialName("function granularity")
data class FunctionPipelineConfig(
    override val inputDir: String,
    override val outputDir: String,
    @SerialName("parser")
    override val parserConfig: ParserConfig,
    @SerialName("filters")
    val filterConfigs: List<FunctionFilterConfig> = emptyList(),
    @SerialName("problem")
    val problemConfig: FunctionProblemConfig,
    @SerialName("storage")
    override val storageConfig: StorageConfig
) : PipelineConfig()

@Serializable
data class ParserConfig(
    val type: String,
    val extensions: List<String>
)

