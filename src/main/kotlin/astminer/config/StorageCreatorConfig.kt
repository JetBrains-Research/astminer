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
@SerialName("csv ast")
object CsvAstStorageCreatorConfig : StorageCreatorConfig() {
    override fun getCreator(outputFolderPath: String) = CsvAstStorageCreator(outputFolderPath)
}

@Serializable
@SerialName("dot ast")
data class DotAstStorageCreatorConfig(val tokenProcessor: TokenProcessor = TokenProcessor.Normalize) :
    StorageCreatorConfig() {
    override fun getCreator(outputFolderPath: String) = DotAstStorageCreator(outputFolderPath, tokenProcessor)
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
