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

class StorageCreator(private val config: StorageConfig, outputDirectoryPath: String) {
    private val outputDirectory = File(outputDirectoryPath)

    private fun getOutputPath(extension: String): String {
        val outputDirectoryForExtension = outputDirectory.resolve(extension)
        outputDirectoryForExtension.mkdir()
        return outputDirectoryForExtension.path
    }

    fun createStorage(extension: String): Storage {
        val outputPath = getOutputPath(extension)
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
