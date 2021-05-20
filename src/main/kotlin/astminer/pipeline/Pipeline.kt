package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.config.*
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

    private val branch = when (config) {
        is FilePipelineConfig -> FilePipelineBranch(config)
        is FunctionPipelineConfig -> FunctionPipelineBranch(config)
    }

    private fun createStorageDirectory(extension: FileExtension): File {
        val outputDirectoryForExtension = outputDirectory.resolve(extension.fileExtension)
        outputDirectoryForExtension.mkdir()
        return outputDirectoryForExtension
    }

    private fun createStorage(extension: FileExtension): Storage = with(config.storage) {
        val storagePath = createStorageDirectory(extension).path

        // TODO: should be removed this later and be implemented like filters and problems, once storage constructors have no side effects
        when (this) {
            is AstStorageConfig -> {
                val tokenProcessor = if (splitTokens) TokenProcessor.Split else TokenProcessor.Normalize
                when (format) {
                    AstStorageFormat.Csv -> CsvAstStorage(storagePath)
                    AstStorageFormat.Dot -> DotAstStorage(storagePath, tokenProcessor)
                }
            }
            is Code2VecPathStorageConfig -> {
                Code2VecPathStorage(storagePath, pathBasedStorageConfig)
            }
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
