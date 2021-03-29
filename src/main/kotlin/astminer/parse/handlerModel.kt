package astminer.parse

import astminer.common.model.*
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
        parseResult.root?.preOrder()?.forEach { node -> processNodeToken(node, splitTokens) }
        return this
    }

    private fun processNodeToken(node: Node, splitToken: Boolean) {
        if (splitToken) {
            node.setNormalizedToken(separateToken(node.getToken()))
        } else {
            node.setNormalizedToken()
        }
    }

    private fun separateToken(token: String, separator: CharSequence = "|"): String {
        return splitToSubtokens(token).joinToString(separator)
    }
}
