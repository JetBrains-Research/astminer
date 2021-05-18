package astminer.config

import astminer.pipeline.*
import astminer.storage.TokenProcessor
import astminer.storage.path.PathBasedStorageConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class StorageCreatorConfig {
    abstract fun getCreator(outputFolderPath: String): StorageCreator
}

@Serializable
enum class AstStorageFormat {
    @SerialName("dot") Dot,
    @SerialName("csv") Csv
}

@Serializable
@SerialName("ast")
data class AstStorageCreatorConfig(
    val format: AstStorageFormat,
    val splitTokens: Boolean = false
) : StorageCreatorConfig() {
    private val tokenProcessor = if (splitTokens) TokenProcessor.Split else TokenProcessor.Normalize

    override fun getCreator(outputFolderPath: String): StorageCreator = when (format) {
        AstStorageFormat.Csv -> CsvAstStorageCreator(outputFolderPath)
        AstStorageFormat.Dot -> DotAstStorageCreator(outputFolderPath, tokenProcessor)
    }
}

@Serializable
@SerialName("code2vec paths")
data class Code2VecPathStorageCreatorConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null,
    val tokenProcessor: TokenProcessor = TokenProcessor.Normalize
) : StorageCreatorConfig() {
    override fun getCreator(outputFolderPath: String) = Code2VecStorageCreator(
        outputFolderPath,
        PathBasedStorageConfig(maxPathLength, maxPathWidth, maxTokens, maxPaths, maxPathContextsPerEntity),
        tokenProcessor
    )
}
