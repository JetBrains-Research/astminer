package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.common.model.FileLabelExtractor
import astminer.common.model.FunctionLabelExtractor
import astminer.common.model.Storage
import astminer.common.model.findDatasetHoldouts
import astminer.config.FileExtension
import astminer.config.PipelineConfig
import astminer.parse.getParsingResultFactory
import astminer.pipeline.branch.FilePipelineBranch
import astminer.pipeline.branch.FunctionPipelineBranch
import astminer.pipeline.branch.IllegalLabelExtractorException
import me.tongfei.progressbar.ProgressBar
import java.io.File

/**
 * Pipeline runs all the steps needed to parse, process and save data.
 * @param config The pipeline config that defines the pipeline
 */
class Pipeline(private val config: PipelineConfig) {
    private val inputDirectory = File(config.inputDir)
    private val outputDirectory = File(config.outputDir)

    private val filters = config.filters.map { it.filterImpl }
    private val labelExtractor = config.labelExtractor.labelExtractorImpl

    private val branch = when (labelExtractor) {
        is FileLabelExtractor -> FilePipelineBranch(filters, labelExtractor)
        is FunctionLabelExtractor -> FunctionPipelineBranch(filters, labelExtractor)
        else -> throw IllegalLabelExtractorException(labelExtractor::class.simpleName)
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
     * Runs the pipeline that is defined in the [config].
     */
    fun run() {
        println("Working in ${config.numOfThreads} thread(s)")
        val holdouts = findDatasetHoldouts(inputDirectory)
        for (language in config.parser.languages) {
            println("Parsing $language")
            val parsingResultFactory = getParsingResultFactory(language, config.parser.name)

            val progressBar = ProgressBar("", files.size.toLong())

            createStorage(language).use { storage ->
                for ((holdoutType, holdoutDir) in holdouts) {
                synchronized(storage) {
                    val holdoutFiles = getProjectFilesWithExtension(holdoutDir, language.fileExtension)
                    parsingResultFactory.parseFilesInThreads(files, config.numOfThreads) { parseResult ->
                        for (labeledResult in branch.process(parseResult)) {
                            storage.store(labeledResult, holdoutType)
                        }
                        progressBar.step()
                    }
                } }
            }
            progressBar.close()
        }
        println("Done!")
    }
}
