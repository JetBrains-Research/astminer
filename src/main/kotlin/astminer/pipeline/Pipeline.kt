package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.common.model.*
import astminer.config.FileExtension
import astminer.config.PipelineConfig
import astminer.config.StorageConfig
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

    private val holdoutMap = findDatasetHoldouts(inputDirectory)
    private val isDataset = holdoutMap.size > 1

    private val branch = when (labelExtractor) {
        is FileLabelExtractor -> FilePipelineBranch(filters, labelExtractor)
        is FunctionLabelExtractor -> FunctionPipelineBranch(filters, labelExtractor)
        else -> throw IllegalLabelExtractorException(labelExtractor::class.simpleName)
    }

    /**
     * Runs the pipeline that is defined in the [config].
     */
    fun run() {
        println("Working in ${config.numOfThreads} thread(s)")
        if (isDataset) { println("Dataset structure found") }
        for (language in config.parser.languages) {
            println("Parsing $language")
            parseLanguage(language)
        }
        println("Done!")
    }

    private fun parseLanguage(language: FileExtension) {
        val parsingResultFactory = getParsingResultFactory(language, config.parser.name)
        val dataStorage = createStorage(config.storage, language)
        val metaDataStorage = createStorage(config.metadata, language)
        try {
            for ((holdoutType, holdoutDir) in holdoutMap) {
                val holdoutFiles = getProjectFilesWithExtension(holdoutDir, language.fileExtension)
                printHoldoutStat(holdoutFiles, holdoutType)
                val progressBar = ProgressBar("", holdoutFiles.size.toLong())
                parsingResultFactory.parseFilesInThreads(holdoutFiles, config.numOfThreads, inputDirectory.path) {
                    val labeledResults = branch.process(it).let { results ->
                        if (config.compressBeforeSaving) { results.toStructurallyNormalized() } else { results }
                    }
                    synchronized(this) {
                        dataStorage.store(labeledResults, holdoutType)
                        metaDataStorage.store(labeledResults, holdoutType)
                    }
                    progressBar.step()
                }
                progressBar.close()
            }
        } finally {
            dataStorage.close()
            metaDataStorage.close()
        }
    }

    private fun createStorage(storageConfig: StorageConfig, extension: FileExtension): Storage {
        val storagePath = createStorageDirectory(extension).path
        return storageConfig.createStorage(storagePath)
    }

    private fun createStorageDirectory(extension: FileExtension): File {
        val outputDirectoryForExtension = outputDirectory.resolve(extension.fileExtension)
        outputDirectoryForExtension.mkdir()
        return outputDirectoryForExtension
    }

    private fun printHoldoutStat(files: List<File>, holdoutType: DatasetHoldout) {
        val output = StringBuilder("${files.size} file(s) found")
        if (isDataset) { output.append(" in ${holdoutType.name}") }
        println(output.toString())
    }
}
