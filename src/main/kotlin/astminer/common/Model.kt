package astminer.common

import java.io.File
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
    /**
     * Parse input stream into the tree.
     * @param content - input stream to parse
     * @return root of the tree
     */
    fun parse(content: InputStream): T?

    /**
     * Parse files, received with [getFilesToParse] from [root folder][projectRoot], into trees.
     * @param projectRoot folder containing files to parse
     * @param getFilesToParse lambda expression for getting files to parse from [projectRoot]
     * @return list of tree's roots for each parsed file
     */
    fun parseProject(projectRoot: File, getFilesToParse: (File) -> List<File>) : List<T?>
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
