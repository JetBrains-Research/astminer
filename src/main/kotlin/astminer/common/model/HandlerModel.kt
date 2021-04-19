package astminer.common.model

import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.common.splitToSubtokens
import java.io.File

interface HandlerFactory {
    fun createHandler(file: File): LanguageHandler<out Node>
}

abstract class LanguageHandler<T: Node> {
    abstract val parseResult: ParseResult<T>
    protected abstract val splitter: TreeMethodSplitter<T>

    fun splitIntoMethods(): Collection<MethodInfo<out Node>> {
        val root = parseResult.root ?: return emptyList()
        return splitter.splitIntoMethods(root)
    }

    fun normalizeParseResult(splitTokens: Boolean): LanguageHandler<T> {
        parseResult.normalize(splitTokens)
        return this
    }
}
