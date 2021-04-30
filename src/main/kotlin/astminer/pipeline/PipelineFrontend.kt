package astminer.pipeline

import astminer.common.model.*
import astminer.parse.getHandlerFactory
import java.io.File

interface PipelineFrontend<T> {
    fun parseEntities(files: List<File>): Sequence<T>
}

abstract class CompositePipelineFrontend<T>(private val parserType: String, extensions: List<String>) :
    PipelineFrontend<T> {

    private val handlerFactories = extensions.associateWith { getHandlerFactory(it, parserType) }

    private val File.handler: LanguageHandler<out Node>?
        get() = handlerFactories[extension]?.createHandler(this)

    protected abstract fun LanguageHandler<out Node>.getEntities(): Sequence<T>

    override fun parseEntities(files: List<File>): Sequence<T> {
        return files.asSequence().flatMap { file ->
            val handler = file.handler
            if (handler != null) {
                handler.getEntities()
            } else {
                println("Failed")
                emptySequence()
            }
        }
    }
}

class FilePipelineFrontend(parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<ParseResult<out Node>>(
        parserType, extensions
    ) {
    override fun LanguageHandler<out Node>.getEntities(): Sequence<ParseResult<out Node>> = sequenceOf(parseResult)
}

class FunctionPipelineFrontend(parserType: String, extensions: List<String>) :
    CompositePipelineFrontend<FunctionInfo<out Node>>(parserType, extensions) {

    override fun LanguageHandler<out Node>.getEntities(): Sequence<FunctionInfo<out Node>> =
        splitIntoMethods().asSequence()
}
