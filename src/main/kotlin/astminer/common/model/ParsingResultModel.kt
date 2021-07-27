package astminer.common.model

import astminer.parse.ParsingException
import mu.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger("HandlerFactory")

interface ParsingResultFactory {
    fun parse(file: File): ParsingResult<out Node>

    fun parseFiles(files: List<File>, action: (ParsingResult<out Node>) -> Unit) {
        for (file in files) {
            try {
                action(parse(file))
            } catch (parsingException: ParsingException) {
                logger.error(parsingException) { "Failed to parse file ${file.path}" }
            }
        }
    }
}

abstract class ParsingResult<T : Node>(internal val file: File) {
    abstract val root: T
    protected abstract val splitter: TreeFunctionSplitter<T>

    fun splitIntoFunctions(): Collection<FunctionInfo<out Node>> =
        splitter.splitIntoFunctions(root, file.path)
}
