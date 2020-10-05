package astminer.common.model

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

    fun getChildrenOfType(typeLabel: String) = getChildren().filter { it.getTypeLabel() == typeLabel }
    fun getChildOfType(typeLabel: String) = getChildrenOfType(typeLabel).firstOrNull()

    fun removeChildrenOfType(typeLabel: String)
}

interface Parser<T : Node> {
    /**
     * Parse input stream into an AST.
     * @param content input stream to parse
     * @return root of the AST
     */
    fun parse(content: InputStream): T?

    /**
     * Parse input stream into an AST.
     * @param file file to parse
     * @return ParseResult instance 
     */
    fun parse(file: File) = ParseResult(parse(file.inputStream()), file.path)

    /**
     * Parse list of files.
     * @param files files to parse
     * @return list of ParseResult instances, one for each parsed file
     */
    fun parse(files: List<File>): List<ParseResult<T>> = files.map { ParseResult(parse(it.inputStream()), it.path) }

    /**
     * Parse list of files.
     * @param files files to parse
     * @param handleResult handler to invoke on each file parse result
     */
    fun parse(files: List<File>, handleResult: (ParseResult<T>) -> Any) {
        files.forEach { handleResult(parse(it)) }
    }
}

data class ParseResult<T : Node>(val root: T?, val filePath: String)
