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
        return functionInfo.modifiers?.let {modifiers -> excludeModifiers.intersect(modifiers).isEmpty()  }
            ?: throw IllegalStateException("Modifiers wasn't properly parsed")
    }
}

/**
 * Filter that excludes functions that have at least one of annotations from the [excludeAnnotations] list.
 */
class AnnotationFilter(private val excludeAnnotations: List<String>) : FunctionFilter {
    override fun validate(functionInfo: FunctionInfo<out Node>): Boolean {
        return functionInfo.annotations?.let { annotations -> excludeAnnotations.intersect(annotations).isEmpty() }
            ?: throw IllegalStateException("Annotations was not properly parsed")
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
