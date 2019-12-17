package cli

import astminer.common.model.Node

fun typeBasedPredicate(root: Node?, nodeType: String, filteredTokens: List<String>): Boolean {
    root?.getChildrenOfType(nodeType)?.forEach {
        if (it.getToken() in filteredTokens) {
            return false
        }
    }
    return true
}

fun modifiersPredicate(root: Node?, modifiers: List<String>): Boolean = typeBasedPredicate(root, "Modifier", modifiers)

fun annotationsPredicate(root: Node?, annotations: List<String>): Boolean = typeBasedPredicate(
    root?.getChildOfType("MarkerAnnotation"), "SimpleName", annotations
)

fun constructorPredicate(root: Node?, classNames: List<String>): Boolean = typeBasedPredicate(
    root, "SimpleName", classNames
)
