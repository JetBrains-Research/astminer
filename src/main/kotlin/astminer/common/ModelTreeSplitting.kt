package astminer.common


interface TreeSplitter<T : Node> {
    fun split(root: T): Collection<T>
}

interface TreeMethodSplitter<T : Node> {
    fun splitIntoMethods(root: T): Collection<MethodInfo<T>>
}

data class MethodInfo<T>(
        val method: MethodNode<T>,
        val enclosingClass: ClassNode<T>,
        val methodParameters: List<ParameterNode<T>>
)

data class MethodNode<T>(
        val root: T,
        val returnTypeNode: T?,
        val nameNode: T?
)

data class ClassNode<T>(
        val root: T?,
        val nameNode: T?
)

data class ParameterNode<T>(
        val root: T,
        val returnTypeNode: T?,
        val nameNode: T?
)
