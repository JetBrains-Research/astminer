package miningtool.common

import java.io.InputStream

interface Node {
    fun getTypeLabel(): String
    fun getChildren(): List<Node>
    fun getParent(): Node?
    fun getToken(): String
    fun isLeaf(): Boolean

    fun getMetadata(key: String): Any?
    fun setMetadata(key: String, value: Any)
}

abstract class Parser<T: Node> {
    abstract fun parse(content: InputStream): T?
}


interface TreeSplitter<T: Node> {
    fun split(root: T): Collection<T>
}

data class ASTPath(val upwardNodes: List<Node>, val downwardNodes: List<Node>)

enum class Direction{UP, DOWN}
data class NodeType(val typeLabel: String, val direction: Direction)

data class PathContext(val startToken: String, val nodeTypes: List<NodeType>, val endToken: String)

abstract class PathStorage {
    abstract fun store(pathContexts: Collection<PathContext>, entityId: String)
    abstract fun save(directoryPath: String)
}