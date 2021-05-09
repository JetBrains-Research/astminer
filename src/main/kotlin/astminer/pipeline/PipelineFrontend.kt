package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.common.model.*
import astminer.parse.getHandlerFactory
import mu.KotlinLogging
import java.io.File

/**
 * A group of entities that come from the files with the same file extension.
 * @param fileExtension The file extension that all entities share.
 * @param entities The entities that are extracted from the files
 * @see PipelineFrontend for the definition of "entity"
 */
data class EntitiesFromFiles<T>(val fileExtension: String, val entities: Sequence<T>)

/**
 * Extracts entities from files and groups them by file extensions.
 * Entity -- anything that can be extracted from a file of code.
 * @param T The type of entities
 */
interface PipelineFrontend<T> {
    /**
     * Extract entities and group them by file extensions.
     * @see PipelineFrontend for the definition of "entity".
     */
    fun getEntities(): Sequence<EntitiesFromFiles<T>>
}

private val logger = KotlinLogging.logger("PipelineFrontend")

/**
 * Base class for several PipelineFrontend implementations.
 * Finds parsers of type [parserType] for all the given languages by [extensions].
 * Looks for files in [inputDirectoryPath].
 */
abstract class CompositePipelineFrontend<T>(
    private val inputDirectoryPath: String,
    private val parserType: String,
    private val extensions: List<String>
) : PipelineFrontend<T> {

    protected abstract fun LanguageHandler<out Node>.getEntities(): Sequence<T>

    override fun getEntities(): Sequence<EntitiesFromFiles<T>> = sequence {
        val inputDirectory = File(inputDirectoryPath)

        logger.info { "Reading ${inputDirectory.absolutePath}" }

        for (extension in extensions) {
            val handlerFactory = try {
                getHandlerFactory(extension, parserType)
            } catch (e: UnsupportedOperationException) {
                // TODO: log everything
                println("Damn")
                yield(EntitiesFromFiles(extension, emptySequence()))
                continue
            }
            val files = getProjectFilesWithExtension(inputDirectory, extension).asSequence()
            val entities = files.flatMap { file -> handlerFactory.createHandler(file).getEntities() }
            yield(EntitiesFromFiles(extension, entities))
        }
    }
}

/**
 * PipelineFrontend that extracts ParseResult<out Node> from files.
 * Basically, it parses the given files and returns the results.
 * @see ParseResult
 */
class FilePipelineFrontend(inputDirectoryPath: String, parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<ParseResult<out Node>>(inputDirectoryPath, parserType, extensions) {
    override fun LanguageHandler<out Node>.getEntities(): Sequence<ParseResult<out Node>> = sequenceOf(parseResult)
}

/**
 * PipelineFrontend that extracts FunctionInfo<out Node> from files.
 * It parses the files, finds functions in those files and collects information about the functions.
 * @see FunctionInfo
 */
class FunctionPipelineFrontend(inputDirectoryPath: String, parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<FunctionInfo<out Node>>(inputDirectoryPath, parserType, extensions) {

    override fun LanguageHandler<out Node>.getEntities(): Sequence<FunctionInfo<out Node>> =
        splitIntoMethods().asSequence()
}
