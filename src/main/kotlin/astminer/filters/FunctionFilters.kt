package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.splitToSubtokens

interface FunctionFilter : Filter<FunctionInfo<out Node>> {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean
}

class ModifierFilter(private val excludeModifiers: List<String>) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean =
        !excludeModifiers.any { modifier -> modifier in entity.modifiers }
}

class AnnotationFilter(private val excludeAnnotations: List<String>) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean =
        !excludeAnnotations.any { annotation -> annotation in entity.annotations }
}

object ConstructorFilter : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>) = !entity.isConstructor
}

class FunctionNameWordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean {
        // TODO: this is not needed
        return if (maxWordsNumber == -1) {
            true
        } else {
            val name = entity.name
            name != null && splitToSubtokens(name).size <= maxWordsNumber
        }
    }
}

class FunctionAnyNodeWordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter {
    override fun isFiltered(entity: FunctionInfo<out Node>): Boolean {
        // TODO: this is not needed
        return if (maxWordsNumber == -1) {
            true
        } else {
            !entity.root.preOrder().any { node -> splitToSubtokens(node.getToken()).size > maxWordsNumber }
        }
    }
}
