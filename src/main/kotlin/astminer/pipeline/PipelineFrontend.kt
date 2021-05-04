package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.common.model.*
import astminer.parse.getHandlerFactory
import java.io.File

data class EntitiesFromFiles<T>(val fileExtension: String, val entities: Sequence<T>)

interface PipelineFrontend<T> {
    fun getEntities(): Sequence<EntitiesFromFiles<T>>
}

abstract class CompositePipelineFrontend<T>(
    private val inputDirectoryPath: String,
    private val parserType: String,
    private val extensions: List<String>
) : PipelineFrontend<T> {

    protected abstract fun LanguageHandler<out Node>.getEntities(): Sequence<T>

    override fun getEntities(): Sequence<EntitiesFromFiles<T>> = sequence {
        val inputDirectory = File(inputDirectoryPath)

        for (extension in extensions) {
            val handlerFactory = getHandlerFactory(extension, parserType)
            val files = getProjectFilesWithExtension(inputDirectory, extension).asSequence()
            val entities = files.flatMap { file -> handlerFactory.createHandler(file).getEntities() }
            yield(EntitiesFromFiles(extension, entities))
        }
    }
}

class FilePipelineFrontend(inputDirectoryPath: String, parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<ParseResult<out Node>>(inputDirectoryPath, parserType, extensions) {
    override fun LanguageHandler<out Node>.getEntities(): Sequence<ParseResult<out Node>> = sequenceOf(parseResult)
}

class FunctionPipelineFrontend(inputDirectoryPath: String, parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<FunctionInfo<out Node>>(inputDirectoryPath, parserType, extensions) {

    override fun LanguageHandler<out Node>.getEntities(): Sequence<FunctionInfo<out Node>> =
        splitIntoMethods().asSequence()
}
