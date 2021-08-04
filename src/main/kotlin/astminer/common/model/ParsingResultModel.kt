package astminer.common.model

import astminer.parse.ParsingException
import me.tongfei.progressbar.ProgressBar
import mu.KotlinLogging
import java.io.File
import kotlin.concurrent.thread

private val logger = KotlinLogging.logger("HandlerFactory")
private const val NUM_OF_THREADS = 16

interface ParsingResultFactory {
    fun parse(file: File): ParsingResult<out Node>

    fun <T> parseFiles(
        files: List<File>,
        progressBar: ProgressBar? = null,
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
            progressBar?.step()
        }
        return results
    }

    fun <T> parseFilesAsync(files: List<File>, action: (ParsingResult<out Node>) -> T): List<T?> {
        val results = mutableListOf<T?>()
        val threads = mutableListOf<Thread>()
        val progressBar = ProgressBar("Parsing progress:", files.size.toLong())

        synchronized(results) {
            files.chunked(files.size / NUM_OF_THREADS + 1).filter { it.isNotEmpty() }
                .map { chunk ->
                    threads.add(thread { results.addAll(parseFiles(chunk, progressBar, action)) })
                }
        }
        threads.map { it.join() }
        progressBar.close()
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
        progressBar: ProgressBar?,
        action: (ParsingResult<out Node>) -> T
    ) =
        files.map { file ->
            progressBar?.step()
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
