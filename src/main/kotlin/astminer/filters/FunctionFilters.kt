package astminer.filters

import astminer.common.model.FunctionFilter
import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.splitToSubtokens

/**
 * Filter that excludes functions that have at least one of modifiers from the [excludeModifiers] list.
 */
class ModifierFilter(private val excludeModifiers: List<String>) : FunctionFilter {
    override fun validate(functionInfo: FunctionInfo<out Node>): Boolean {
        val functionModifiers = functionInfo.modifiers ?: return false
        return functionModifiers.none { modifier -> modifier in excludeModifiers }
    }
}

/**
 * Filter that excludes functions that have at least one of annotations from the [excludeAnnotations] list.
 */
class AnnotationFilter(private val excludeAnnotations: List<String>) : FunctionFilter {
    override fun validate(functionInfo: FunctionInfo<out Node>): Boolean {
        val functionAnnotations = functionInfo.annotations ?: return false
        return functionAnnotations.none { annotation -> annotation in excludeAnnotations }
    }
}

/**
 * Filter that excludes constructors.
 */
object ConstructorFilter : FunctionFilter {
    override fun validate(functionInfo: FunctionInfo<out Node>) = !functionInfo.isConstructor
}

/**
 * Filter that excludes functions that have more than [maxWordsNumber] words in their names.
 */
class FunctionNameWordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter {
    override fun validate(functionInfo: FunctionInfo<out Node>): Boolean {
        val name = functionInfo.name
        return name != null && splitToSubtokens(name).size <= maxWordsNumber
    }
}

/**
 * Filter that excludes blank functions (functions without body or with empty body)
 * */
class RemoveBlankFunctions : FunctionFilter {
    override fun validate(functionInfo: FunctionInfo<out Node>): Boolean = functionInfo.isNotBlank()
}
