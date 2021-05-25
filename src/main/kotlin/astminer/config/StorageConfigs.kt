package astminer.config

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
 * @see astminer.storage.ast.CsvAstStorage
 */
@Serializable
@SerialName("CsvAST")
object CsvAstStorageConfig : StorageConfig()

/**
 * @see astminer.storage.ast.DotAstStorage
 */
@Serializable
@SerialName("DotAST")
object DotAstStorageConfig : StorageConfig()

/**
 * Config for [astminer.storage.path.Code2VecPathStorage]
 */
@Serializable
@SerialName("Code2vec")
data class Code2VecPathStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null,
) : StorageConfig() {
    @Transient
    val pathBasedStorageConfig =
        PathBasedStorageConfig(maxPathLength, maxPathWidth, maxTokens, maxPaths, maxPathContextsPerEntity)
}
