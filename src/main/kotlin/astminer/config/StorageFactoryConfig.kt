package astminer.config

import astminer.pipeline.*
import astminer.storage.TokenProcessor
import astminer.storage.path.PathBasedStorageConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class StorageFactoryConfig {
    abstract fun getCreator(outputFolderPath: String): StorageFactory
}

@Serializable
enum class AstStorageFormat {
    @SerialName("dot") Dot,
    @SerialName("csv") Csv
}

@Serializable
@SerialName("ast")
data class AstStorageFactoryConfig(
    val format: AstStorageFormat,
    val splitTokens: Boolean = false
) : StorageFactoryConfig() {
    private val tokenProcessor = if (splitTokens) TokenProcessor.Split else TokenProcessor.Normalize

    override fun getCreator(outputFolderPath: String): StorageFactory = when (format) {
        AstStorageFormat.Csv -> CsvAstStorageFactory(outputFolderPath)
        AstStorageFormat.Dot -> DotAstStorageFactory(outputFolderPath, tokenProcessor)
    }
}

@Serializable
@SerialName("code2vec paths")
data class Code2VecPathStorageFactoryConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val maxTokens: Long? = null,
    val maxPaths: Long? = null,
    val maxPathContextsPerEntity: Int? = null,
    val tokenProcessor: TokenProcessor = TokenProcessor.Normalize
) : StorageFactoryConfig() {
    override fun getCreator(outputFolderPath: String) = Code2VecStorageFactory(
        outputFolderPath,
        PathBasedStorageConfig(maxPathLength, maxPathWidth, maxTokens, maxPaths, maxPathContextsPerEntity),
        tokenProcessor
    )
}
