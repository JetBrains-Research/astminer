package astminer.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PipelineConfig

@Serializable
@SerialName("file granularity")
data class FilePipelineConfig(
    val inputDir: String,
    val outputDir: String,
    @SerialName("parser") val parserConfig: ParserConfig,
    @SerialName("filters") val filterConfigs: List<FileFilterConfig> = emptyList(),
    @SerialName("problem") val problemConfig: FileProblemConfig,
    val excludedNodeTypes: List<String> = emptyList(),
    @SerialName("storage") val storageFactoryConfig: StorageFactoryConfig
) : PipelineConfig()

@Serializable
@SerialName("function granularity")
data class FunctionPipelineConfig(
    val inputDir: String,
    val outputDir: String,
    @SerialName("parser") val parserConfig: ParserConfig,
    @SerialName("filters") val filterConfigs: List<FunctionFilterConfig> = emptyList(),
    @SerialName("problem") val problemConfig: FunctionProblemConfig,
    val excludedNodeTypes: List<String> = emptyList(),
    @SerialName("storage") val storageFactoryConfig: StorageFactoryConfig
) : PipelineConfig()

@Serializable
data class ParserConfig(
    val type: String,
    val extensions: List<String>
)

