package astminer.common.model

interface TreeMethodSplitter<T : Node> {
    fun splitIntoMethods(root: T): Collection<MethodInfo<T>>
}

class MethodInfo<T : Node>(
        val method: MethodNode<T>,
        val enclosingElement: ElementNode<T>,
        val methodParameters: List<ParameterNode<T>>
) {
    fun name() = method.name()
    fun returnType() = method.returnType()

    fun enclosingElementName() = enclosingElement.name()
}

class MethodNode<T : Node>(
        val root: T,
        val returnTypeNode: T?,
        val nameNode: T?
) {
    fun name() = nameNode?.token
    fun returnType() = returnTypeNode?.token
}

class ElementNode<T : Node>(
        val root: T?,
        val nameNode: T?
) {
    fun name() = nameNode?.token
}

data class ParameterNode<T : Node>(
        val root: T,
        val returnTypeNode: T?,
        val nameNode: T?
) {
    fun name() = nameNode?.token
    fun returnType() = returnTypeNode?.token
}
