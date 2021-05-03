package astminer.config

import astminer.storage.Storage
import astminer.storage.TokenProcessor
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig

sealed class StorageConfig {
    abstract fun getStorage(outputDirectoryPath: String): Storage
}

object CsvAstStorageConfig : StorageConfig() {
    override fun getStorage(outputDirectoryPath: String) = CsvAstStorage(outputDirectoryPath)
}

data class DotAstStorageConfig(val tokenProcessor: TokenProcessor) : StorageConfig() {
    override fun getStorage(outputDirectoryPath: String) = DotAstStorage(outputDirectoryPath, tokenProcessor)
}

data class Code2VecPathStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null,
    val tokenProcessor: TokenProcessor
) : StorageConfig() {

    private val storageConfig = PathBasedStorageConfig(
        maxPathLength,
        maxPathWidth,
        maxTokens,
        maxPaths,
        maxPathContextsPerEntity
    )

    override fun getStorage(outputDirectoryPath: String) =
        Code2VecPathStorage(outputDirectoryPath, storageConfig, tokenProcessor)
}
