package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.config.*
import astminer.filters.ModifierFilter
import astminer.filters.TreeSizeFilter
import astminer.parse.ParsingException
import astminer.parse.getHandlerFactory
import astminer.storage.Storage
import astminer.storage.TokenProcessor
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.path.Code2VecPathStorage
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger("Pipeline")

/**
 * Pipeline runs all the steps needed to parse, process and save data.
 * @param config The pipeline config that defines the pipeline
 */
class Pipeline(private val config: PipelineConfig) {
    private val inputDirectory = File(config.inputDir)
    private val outputDirectory = File(config.outputDir)

    private val branch = when (config.problem.granularity) {
        Granularity.File -> FilePipelineBranch(config)
        Granularity.Function -> FunctionPipelineBranch(config)
    }

    private fun createStorageDirectory(extension: FileExtension): File {
        val outputDirectoryForExtension = outputDirectory.resolve(extension.fileExtension)
        outputDirectoryForExtension.mkdir()
        return outputDirectoryForExtension
    }

    private fun createStorage(extension: FileExtension): Storage = with(config.storage) {
        val storagePath = createStorageDirectory(extension).path
        when (this) {
            is CsvAstStorageConfig -> CsvAstStorage(storagePath)
            is DotAstStorageConfig -> DotAstStorage(storagePath, TokenProcessor.Split)
            is Code2VecPathStorageConfig -> Code2VecPathStorage(storagePath, pathBasedStorageConfig)
        }
    }

    /**
     * Runs the pipeline that is defined in the [config]
     */
    fun run() {
        for (extension in config.parser.extensions) {
            val languageFactory = getHandlerFactory(extension, config.parser.type)

            val files = getProjectFilesWithExtension(inputDirectory, extension.fileExtension).asSequence()
            val labeledResults = files.mapNotNull { file ->
                try {
                    languageFactory.createHandler(file)
                } catch (e: ParsingException) {
                    logger.error(e) { "Failed to parse file ${file.path}" }
                    null
                }
            }.flatMap { branch.process(it) }

            createStorage(extension).use { storage ->
                storage.store(labeledResults.asIterable())
            }
        }
    }
}
