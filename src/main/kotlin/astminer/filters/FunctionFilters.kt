package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.splitToSubtokens

interface FunctionFilter {
    fun test(functionInfo: FunctionInfo<out Node>): Boolean
}

/**
 * Filter that excludes functions that have at least one of modifiers from the [excludeModifiers] list.
 */
class ModifierFilter(private val excludeModifiers: List<String>) : FunctionFilter {
    override fun test(functionInfo: FunctionInfo<out Node>): Boolean =
        !excludeModifiers.any { modifier -> modifier in functionInfo.modifiers }
}

/**
 * Filter that excludes functions that have at least one annotations from the [excludeAnnotations] list.
 */
class AnnotationFilter(private val excludeAnnotations: List<String>) : FunctionFilter {
    override fun test(functionInfo: FunctionInfo<out Node>): Boolean =
        !excludeAnnotations.any { annotation -> annotation in functionInfo.annotations }
}

/**
 * Filter that excludes constructors
 */
object ConstructorFilter : FunctionFilter {
    override fun test(functionInfo: FunctionInfo<out Node>) = !functionInfo.isConstructor
}

/**
 * Filter that excludes functions that have more than [maxWordsNumber] words in their names.
 */
class FunctionNameWordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter {
    override fun test(functionInfo: FunctionInfo<out Node>): Boolean {
        val name = functionInfo.name
        return name != null && splitToSubtokens(name).size <= maxWordsNumber
    }
}

/**
 * Filter that excludes functions that have more words than [maxWordsNumber] in any token of their subtree.
 */
class FunctionAnyNodeWordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter {
    override fun test(functionInfo: FunctionInfo<out Node>): Boolean =
        !functionInfo.root.preOrder().any { node -> splitToSubtokens(node.getToken()).size > maxWordsNumber }

}
