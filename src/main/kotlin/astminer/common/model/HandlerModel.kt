package astminer.common.model

import java.io.File

interface HandlerFactory {
    fun createHandler(file: File): LanguageHandler<out Node>
}

abstract class LanguageHandler<T : Node> {
    abstract val parseResult: ParseResult<T>
    protected abstract val splitter: TreeMethodSplitter<T>

    fun splitIntoMethods(): Collection<FunctionInfo<out Node>> {
        return splitter.splitIntoMethods(parseResult.root)
    }
}
