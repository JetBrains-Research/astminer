package astminer.pipeline

import astminer.storage.Storage
import astminer.storage.TokenProcessor
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

interface StorageFactory {
    fun createStorageAndOutputFolder(extension: String): Storage
}

abstract class AbstractStorageFactory(private val outputDirectoryPath: String) : StorageFactory {
    private fun createOutputFolder(extension: String): File {
        val outputDirectoryForExtension = File(outputDirectoryPath).resolve(extension)
        outputDirectoryForExtension.mkdir()
        return outputDirectoryForExtension
    }

    abstract fun initializeStorage(outputFolderPath: String): Storage

    override fun createStorageAndOutputFolder(extension: String): Storage =
        initializeStorage(createOutputFolder(extension).path)
}

/**
 * Creates CsvAstStorages
 */
class CsvAstStorageFactory(outputDirectoryPath: String) : AbstractStorageFactory(outputDirectoryPath) {
    override fun initializeStorage(outputFolderPath: String) = CsvAstStorage(outputFolderPath)
}

/**
 * Creates DotAstStorages given [tokenProcessor]
 */
class DotAstStorageFactory(outputDirectoryPath: String, private val tokenProcessor: TokenProcessor) :
    AbstractStorageFactory(outputDirectoryPath) {
    override fun initializeStorage(outputFolderPath: String) = DotAstStorage(outputFolderPath, tokenProcessor)
}

/**
 * Creates Code2VecStorages given [config] and [tokenProcessor]
 */
class Code2VecStorageFactory(
    outputDirectoryPath: String,
    private val config: PathBasedStorageConfig,
    private val tokenProcessor: TokenProcessor
) : AbstractStorageFactory(outputDirectoryPath) {
    override fun initializeStorage(outputFolderPath: String) =
        Code2VecPathStorage(outputFolderPath, config, tokenProcessor)
}

