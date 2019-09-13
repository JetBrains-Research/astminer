package astminer.common


interface TreeSplitter<T : Node> {
    fun split(root: T): Collection<T>
}
