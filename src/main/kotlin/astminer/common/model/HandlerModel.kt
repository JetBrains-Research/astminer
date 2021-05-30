package astminer.common.model

import astminer.parse.ParsingException
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger("HandlerFactory")

interface HandlerFactory {
    fun createHandler(file: File): LanguageHandler<out Node>

    fun createHandlers(files: List<File>, handleResult: (LanguageHandler<out Node>) -> Any?) {
        for (file in files) {
            try {
                handleResult(createHandler(file))
            } catch (parsingException: ParsingException) {
                logger.error(parsingException) { "Failed to parse file ${file.path}" }
            }
        }
    }
}

abstract class LanguageHandler<T : Node> {
    abstract val parseResult: ParseResult<T>
    protected abstract val splitter: TreeFunctionSplitter<T>

    fun splitIntoFunctions(): Collection<FunctionInfo<out Node>> {
        return splitter.splitIntoFunctions(parseResult.root)
    }
}
