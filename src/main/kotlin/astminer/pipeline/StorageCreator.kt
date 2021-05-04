package astminer.pipeline

import astminer.config.Code2VecPathStorageConfig
import astminer.config.CsvAstStorageConfig
import astminer.config.DotAstStorageConfig
import astminer.config.StorageConfig
import astminer.storage.Storage
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.path.Code2VecPathStorage
import java.io.File

/**
 * Creates storage for each extension.
 * @param config The config that defines that storage will be used and the params of that storage
 * @param outputDirectoryPath Path to the base output directory where folders for each extension will be created
 *                            (e.g 'py', 'java')
 */
class StorageCreator(private val config: StorageConfig, outputDirectoryPath: String) {
    private val outputDirectory = File(outputDirectoryPath)

    private fun createOutputPath(extension: String): String {
        val outputDirectoryForExtension = outputDirectory.resolve(extension)
        outputDirectoryForExtension.mkdir()
        return outputDirectoryForExtension.path
    }

    /**
     * Creates folder [outputDirectoryPath]/[extension] and initializes the storage in that folder.
     */
    fun createStorageAndOutputFolder(extension: String): Storage {
        val outputPath = createOutputPath(extension)
        return when (config) {
            is CsvAstStorageConfig -> CsvAstStorage(outputPath)
            is DotAstStorageConfig -> DotAstStorage(outputPath, config.tokenProcessor)
            is Code2VecPathStorageConfig -> Code2VecPathStorage(
                outputPath,
                config.toPathBasedConfig(),
                config.tokenProcessor
            )
        }
    }
}
