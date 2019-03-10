package astminer.common

import java.io.InputStream

interface Node {
    fun getTypeLabel(): String
    fun getChildren(): List<Node>
    fun getParent(): Node?
    fun getToken(): String
    fun isLeaf(): Boolean

    fun getMetadata(key: String): Any?
    fun setMetadata(key: String, value: Any)
    fun prettyPrint(indent: Int = 0, indentSymbol: String = "--") {
        repeat(indent) { print(indentSymbol) }
        print(getTypeLabel())
        if (getToken().isNotEmpty()) {
            println(" : ${getToken()}")
        } else {
            println()
        }
        getChildren().forEach { it.prettyPrint(indent + 1, indentSymbol) }
    }
}

interface Parser<T : Node> {
    fun parse(content: InputStream): T?
}

interface TreeSplitter<T : Node> {
    fun split(root: T): Collection<T>
}

data class ASTPath(val upwardNodes: List<Node>, val downwardNodes: List<Node>)

enum class Direction { UP, DOWN }

data class OrientedNodeType(val typeLabel: String, val direction: Direction)

data class PathContext(val startToken: String, val orientedNodeTypes: List<OrientedNodeType>, val endToken: String)

/**
 * Stores path-contexts and saves them to directory.
 */
interface PathStorage {
    fun store(pathContexts: Collection<PathContext>, entityId: String)
    fun save(directoryPath: String)
}

/**
 * Stores ASTs in form of their root and saves them to directory.
 */
interface AstStorage {
    fun store(root: Node, entityId: String)
    fun save(directoryPath: String)
}
