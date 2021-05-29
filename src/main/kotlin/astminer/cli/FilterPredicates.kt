package astminer.cli

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.splitToSubtokens

interface MethodFilter {
    fun isFiltered(functionInfo: FunctionInfo<out Node>): Boolean
}

class ModifierFilterPredicate(private val excludeModifiers: List<String>) : MethodFilter {
    override fun isFiltered(functionInfo: FunctionInfo<out Node>): Boolean =
        !excludeModifiers.any { modifier -> modifier in functionInfo.modifiers }
}

class AnnotationFilterPredicate(private val excludeAnnotations: List<String>) : MethodFilter {
    override fun isFiltered(functionInfo: FunctionInfo<out Node>): Boolean =
        !excludeAnnotations.any { annotation -> annotation in functionInfo.annotations }
}

object ConstructorFilterPredicate : MethodFilter {
    override fun isFiltered(functionInfo: FunctionInfo<out Node>) = !functionInfo.isConstructor
}

class MethodNameWordsNumberFilter(private val maxWordsNumber: Int) : MethodFilter {
    override fun isFiltered(functionInfo: FunctionInfo<out Node>): Boolean {
        return if (maxWordsNumber == -1) {
            true
        } else {
            val name = functionInfo.name
            name != null && splitToSubtokens(name).size <= maxWordsNumber
        }
    }
}

class MethodAnyNodeWordsNumberFilter(private val maxWordsNumber: Int) : MethodFilter {
    override fun isFiltered(functionInfo: FunctionInfo<out Node>): Boolean {
        return if (maxWordsNumber == -1) {
            true
        } else {
            !functionInfo.root.preOrder().any { node ->
                node.normalizedToken?.let { it.split("|").size > maxWordsNumber } ?: false
            }
        }
    }
}

class TreeSizeFilterPredicate(private val maxSize: Int) : MethodFilter {
    override fun isFiltered(functionInfo: FunctionInfo<out Node>): Boolean {
        return if (maxSize == -1) {
            true
        } else {
            functionInfo.root.preOrder().size <= maxSize
        }
    }
}
