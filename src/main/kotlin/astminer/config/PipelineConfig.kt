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
    val parserConfig: ParserConfig,
    val filterConfigs: List<FileFilterConfig> = emptyList(),
    val problemConfig: FileProblemConfig,
    val excludedNodeTypes: List<String> = emptyList(),
    val storageCreatorConfig: StorageCreatorConfig
) : PipelineConfig()

@Serializable
@SerialName("function granularity")
data class FunctionPipelineConfig(
    val inputDir: String,
    val outputDir: String,
    val parserConfig: ParserConfig,
    val filterConfigs: List<FunctionFilterConfig> = emptyList(),
    val problemConfig: FunctionProblemConfig,
    val excludedNodeTypes: List<String> = emptyList(),
    val storageCreatorConfig: StorageCreatorConfig
) : PipelineConfig()

@Serializable
data class ParserConfig(
    val type: String,
    val extensions: List<String>
)

