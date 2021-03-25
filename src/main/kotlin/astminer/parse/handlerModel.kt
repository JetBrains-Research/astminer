package astminer.parse

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.Parser

interface LanguageHandler {
    fun splitIntoMethods(root: Node): Collection<MethodInfo<out Node>>
    val parser: Parser<out Node>
}
