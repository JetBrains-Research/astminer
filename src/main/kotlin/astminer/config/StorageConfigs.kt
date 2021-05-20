package astminer.config

import astminer.storage.TokenProcessor
import astminer.storage.path.PathBasedStorageConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Config for storage that saved the results on the disk
 */
@Serializable
sealed class StorageConfig

/**
 * Format in which the ASTs should be saved
 * [Dot] is for [astminer.storage.ast.DotAstStorage]
 * [Csv] is for [astminer.storage.ast.CsvAstStorage]
 */
@Serializable
enum class AstStorageFormat {
    @SerialName("dot")
    Dot,
    @SerialName("csv")
    Csv
}

/**
 * Config for [astminer.storage.ast.DotAstStorage] [astminer.storage.ast.CsvAstStorage]
 */
@Serializable
@SerialName("ast")
data class AstStorageConfig(
    val format: AstStorageFormat,
    val splitTokens: Boolean = false
) : StorageConfig()

/**
 * Config for [astminer.storage.path.Code2VecPathStorage]
 */
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
