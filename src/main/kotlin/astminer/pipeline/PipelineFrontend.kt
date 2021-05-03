package astminer.pipeline

import astminer.common.model.*
import astminer.parse.getHandlerFactory
import java.io.Closeable
import java.io.File

data class EntitiesFromFiles<T>(val fileExtension: String, val entities: Sequence<T>)

interface PipelineFrontend<T> {
    val inputDirectory: File
    fun getEntities(): Sequence<EntitiesFromFiles<T>>
}

abstract class CompositePipelineFrontend<T>(
    private val projectImporter: ProjectImporter,
    private val parserType: String,
    private val extensions: List<String>
) :
    PipelineFrontend<T>, Closeable {

    override val inputDirectory: File = projectImporter.projectDirectory

    private val handlerFactories = extensions.associateWith { getHandlerFactory(it, parserType) }

    private val File.handler: LanguageHandler<out Node>?
        get() = handlerFactories[extension]?.createHandler(this)

    protected abstract fun LanguageHandler<out Node>.getEntities(): Sequence<T>

    private fun getEntities(files: Sequence<File>): Sequence<T> {
        return files.flatMap { file ->
            val handler = file.handler
            if (handler != null) {
                handler.getEntities()
            } else {
                println("Failed")
                emptySequence()
            }
        }
    }

    override fun getEntities(): Sequence<EntitiesFromFiles<T>> = sequence {
        for (extension in extensions) {
            val files = projectImporter.getFiles(extension)
            val entities = getEntities(files)
            yield(EntitiesFromFiles(extension, entities))
        }
    }

    override fun close() {
        projectImporter.close()
    }
}

class FilePipelineFrontend(projectImporter: ProjectImporter, parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<ParseResult<out Node>>(
        projectImporter, parserType, extensions
    ) {
    override fun LanguageHandler<out Node>.getEntities(): Sequence<ParseResult<out Node>> = sequenceOf(parseResult)
}

class FunctionPipelineFrontend(projectImporter: ProjectImporter, parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<FunctionInfo<out Node>>(projectImporter, parserType, extensions) {

    override fun LanguageHandler<out Node>.getEntities(): Sequence<FunctionInfo<out Node>> =
        splitIntoMethods().asSequence()
}
