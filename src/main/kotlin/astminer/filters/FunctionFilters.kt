package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.splitToSubtokens

interface FunctionFilter : Filter<FunctionInfo<out Node>> {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean
}

class ModifierFilterPredicate(private val excludeModifiers: List<String>) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean =
        !excludeModifiers.any { modifier -> modifier in entity.modifiers }
}

class AnnotationFilterPredicate(private val excludeAnnotations: List<String>) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean =
        !excludeAnnotations.any { annotation -> annotation in entity.annotations }
}

object ConstructorFilterPredicate : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>) = !entity.isConstructor
}

class MethodNameWordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean {
        return if (maxWordsNumber == -1) {
            true
        } else {
            val name = entity.name
            name != null && splitToSubtokens(name).size <= maxWordsNumber
        }
    }
}

class MethodAnyNodeWordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean {
        return if (maxWordsNumber == -1) {
            true
        } else {
            !entity.root.preOrder().any { node -> splitToSubtokens(node.getToken()).size > maxWordsNumber }
        }
    }
}
