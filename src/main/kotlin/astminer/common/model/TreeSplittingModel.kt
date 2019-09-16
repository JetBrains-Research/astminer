package astminer.common.model


interface TreeSplitter<T : Node> {
    fun split(root: T): Collection<T>
}

interface TreeMethodSplitter<T : Node> {
    fun splitIntoMethods(root: T): Collection<MethodInfo<T>>
}

class MethodInfo<T : Node>(
        val method: MethodNode<T>,
        val enclosingClass: ClassNode<T>,
        val methodParameters: List<ParameterNode<T>>
) {
    fun name() = method.name()
    fun returnType() = method.returnType()

    fun className() = enclosingClass.name()
}

class MethodNode<T : Node>(
        val root: T,
        val returnTypeNode: T?,
        val nameNode: T?
) {
    fun name() = nameNode?.getToken()
    fun returnType() = returnTypeNode?.getToken()
}

class ClassNode<T : Node>(
        val root: T?,
        val nameNode: T?
) {
    fun name() = nameNode?.getToken()
}

data class ParameterNode<T : Node>(
        val root: T,
        val returnTypeNode: T?,
        val nameNode: T?
) {
    fun name() = nameNode?.getToken()
    fun returnType() = returnTypeNode?.getToken()
}
