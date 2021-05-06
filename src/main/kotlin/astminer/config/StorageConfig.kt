package astminer.config

import astminer.storage.TokenProcessor
import astminer.storage.path.PathBasedStorageConfig

sealed class StorageConfig

object CsvAstStorageConfig : StorageConfig()

data class DotAstStorageConfig(val tokenProcessor: TokenProcessor = TokenProcessor.Normalize) : StorageConfig()

data class Code2VecPathStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null,
    val tokenProcessor: TokenProcessor = TokenProcessor.Normalize
) : StorageConfig() {
    fun toPathBasedConfig() =
        PathBasedStorageConfig(maxPathLength, maxPathWidth, maxTokens, maxPaths, maxPathContextsPerEntity)
}
