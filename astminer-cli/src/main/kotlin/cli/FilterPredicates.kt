package cli

import astminer.common.model.MethodInfo
import astminer.common.model.Node

abstract class MethodFilterPredicate {
    open fun isFiltered(methodInfo: MethodInfo<out Node>): Boolean = false

    fun typeBasedFilterPredicate(root: Node?, nodeType: String, excludeValues: List<String>): Boolean {
        root?.getChildrenOfType(nodeType)?.forEach {
            if (it.getToken() in excludeValues) {
                return false
            }
        }
        return true
    }
}

class ModifierFilterPredicate(private val excludeModifiers: List<String>) :
    MethodFilterPredicate() {

    // TODO: add other parsers

    private fun gumTreeModifierFilter(root: Node?) : Boolean =
        typeBasedFilterPredicate(root, "Modifier", excludeModifiers)

    override fun isFiltered(methodInfo: MethodInfo<out Node>): Boolean =
        gumTreeModifierFilter(methodInfo.method.root)
}

class AnnotationFilterPredicate(private val excludeAnnotations: List<String>) :
    MethodFilterPredicate() {

    // TODO: add other parsers

    private fun gumTreeAnnotationFilter(root: Node?) : Boolean =
        typeBasedFilterPredicate(
            root?.getChildOfType("MarkerAnnotation"), "SimpleName", excludeAnnotations
        )

    override fun isFiltered(methodInfo: MethodInfo<out Node>): Boolean =
        gumTreeAnnotationFilter(methodInfo.method.root)
}

class ConstructorFilterPredicate : MethodFilterPredicate() {

    override fun isFiltered(methodInfo: MethodInfo<out Node>): Boolean {
        return methodInfo.name() != methodInfo.enclosingElementName()
    }
}
