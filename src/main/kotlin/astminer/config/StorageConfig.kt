package astminer.config

import astminer.storage.TokenProcessor
import astminer.storage.path.PathBasedStorageConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class StorageConfig

@Serializable
enum class AstStorageFormat {
    @SerialName("dot")
    Dot,
    @SerialName("csv")
    Csv
}

@Serializable
@SerialName("ast")
data class AstStorageConfig(
    val format: AstStorageFormat,
    val splitTokens: Boolean = false
) : StorageConfig()

@Serializable
@SerialName("code2vec paths")
data class Code2VecPathStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null,
    val tokenProcessor: TokenProcessor = TokenProcessor.Normalize
) : StorageConfig() {
    @Transient
    val pathBasedStorageConfig =
        PathBasedStorageConfig(maxPathLength, maxPathWidth, maxTokens, maxPaths, maxPathContextsPerEntity)
}
