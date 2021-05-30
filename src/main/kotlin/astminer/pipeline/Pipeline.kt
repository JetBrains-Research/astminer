package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.config.*
import astminer.parse.getHandlerFactory
import astminer.pipeline.branch.FilePipelineBranch
import astminer.pipeline.branch.FunctionPipelineBranch
import astminer.problem.Granularity
import astminer.storage.Storage
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.path.Code2VecPathStorage
import java.io.File

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

    private fun createStorage(extension: FileExtension): Storage {
        val storagePath = createStorageDirectory(extension).path
        return config.storage.createStorage(storagePath)
    }

    /**
     * Runs the pipeline that is defined in the [config]
     */
    fun run() {
        for (extension in config.parser.extensions) {
            val languageFactory = getHandlerFactory(extension, config.parser.name)

            val files = getProjectFilesWithExtension(inputDirectory, extension.fileExtension)

            createStorage(extension).use { storage ->
                languageFactory.createHandlers(files) { languageHandler ->
                    for (labeledResult in branch.process(languageHandler)) {
                        storage.store(labeledResult)
                    }
                }
            }
        }
    }
}
