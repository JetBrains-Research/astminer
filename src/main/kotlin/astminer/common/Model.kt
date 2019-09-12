package astminer.common

import java.io.File
import java.io.InputStream

interface Node {
    fun getTypeLabel(): String
    fun getChildren(): List<Node>
    fun getParent(): Node?
    fun getToken(): String
    fun setToken(newToken: String)
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
     * Parse input stream into an AST.
     * @param content input stream to parse
     * @return root of the AST
     */
    fun parse(content: InputStream): T?

    /**
     * Parse list of files.
     * @param files files to parse
     * @return list of AST roots, one for each parsed file
     */
    fun parse(files: List<File>): List<ParseResult<T>> = files.map { ParseResult(parse(it.inputStream()), it.path) }

    /**
     * Parse all files that pass [filter][filter] in [root folder][projectRoot] and its sub-folders.
     * @param projectRoot root folder containing files to parse
     * @param filter lambda expression that determines which files should be parsed
     * @return list of AST roots, one for each parsed file
     */
    fun parseProject(projectRoot: File, filter: (File) -> Boolean): List<ParseResult<T>> {
        val files = projectRoot.walkTopDown().filter(filter).toList()
        return parse(files)
    }

    /**
     * Parse all files with given extension in [root folder][projectRoot] and its sub-folders.
     * @param projectRoot root folder containing files to parse
     * @param extension extension of files that should be parsed
     * @return list of AST roots, one for each parsed file
     */
    fun parseWithExtension(projectRoot: File, extension: String) = parseProject(projectRoot) { it.isFile && it.extension == extension }
}

data class ParseResult<T : Node>(val root: T?, val filePath: String)

interface TreeSplitter<T : Node> {
    fun split(root: T): Collection<T>
}

data class ASTPath(val upwardNodes: List<Node>, val downwardNodes: List<Node>)

enum class Direction { UP, DOWN }

data class OrientedNodeType(val typeLabel: String, val direction: Direction)

data class PathContext(val startToken: String, val orientedNodeTypes: List<OrientedNodeType>, val endToken: String)

data class PathContextId(val startTokenId: Long, val pathId: Long, val endTokenId: Long)

data class LabeledPathContexts<T>(val label: T, val pathContexts: Collection<PathContext>)

data class LabeledPathContextIds<T>(val label: T, val pathContexts: Collection<PathContextId>)

/**
 * Stores path-contexts and their labels and saves them to directory.
 */
interface PathStorage<LabelType> {
    fun store(labeledPathContexts: LabeledPathContexts<LabelType>)
    fun save(directoryPath: String)
}

/**
 * Stores ASTs in form of their root and saves them to directory.
 */
interface AstStorage {
    fun store(root: Node, label: String)
    fun save(directoryPath: String)
}
