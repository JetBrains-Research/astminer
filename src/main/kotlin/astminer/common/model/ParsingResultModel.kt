package astminer.common.model

import astminer.parse.ParsingException
import mu.KotlinLogging
import java.io.File
import kotlin.concurrent.thread
import kotlin.math.ceil

private val logger = KotlinLogging.logger("HandlerFactory")

interface ParsingResultFactory {
    fun parse(file: File): ParsingResult<out Node>

    fun <T> parseFiles(
        files: List<File>,
        action: (ParsingResult<out Node>) -> T
    ): List<T?> {
        val results = mutableListOf<T?>()
        files.map { file ->
            try {
                results.add(action(parse(file)))
            } catch (parsingException: ParsingException) {
                logger.error(parsingException) { "Failed to parse file ${file.path}" }
                results.add(null)
            }
        }
        return results
    }

    fun <T> parseFilesInThreads(
        files: List<File>,
        numOfThreads: Int,
        action: (ParsingResult<out Node>) -> T
    ): List<T?> {
        val results = mutableListOf<T?>()
        val threads = mutableListOf<Thread>()

        if (files.isEmpty()) { return emptyList() }

        synchronized(results) {
            files.chunked(ceil(files.size.toDouble() / numOfThreads).toInt()).filter { it.isNotEmpty() }
                .map { chunk ->
                    threads.add(thread { results.addAll(parseFiles(chunk, action)) })
                }
        }
        threads.map { it.join() }
        return results
    }
}

interface PreprocessingParsingResultFactory : ParsingResultFactory {
    fun preprocess(file: File, outputDir: File? = null): File

    /**
     * Run preprocessing and parsing for all files.
     * @param files list of files to be parsed with preprocessing
     * @param action action to do with parsed files (e.g. save on the disk)
     */
    override fun <T> parseFiles(
        files: List<File>,
        action: (ParsingResult<out Node>) -> T
    ) =
        files.map { file ->
            try {
                val preprocessedFile = preprocess(file)
                val result = action(parse(preprocessedFile))
                preprocessedFile.delete()
                result
            } catch (parsingException: ParsingException) {
                logger.error(parsingException) { "Failed to parse file ${file.path}" }
                null
            }
        }
}

abstract class ParsingResult<T : Node>(internal val file: File) {
    abstract val root: T
    protected abstract val splitter: TreeFunctionSplitter<T>

    fun splitIntoFunctions(): Collection<FunctionInfo<out Node>> =
        splitter.splitIntoFunctions(root, file.path)
}
