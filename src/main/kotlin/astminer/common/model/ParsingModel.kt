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
    fun parseInputStream(content: InputStream): T?

    /**
     * Parse file into an AST.
     * @param file file to parse
     * @return ParseResult instance 
     */
    fun parseFile(file: File) = ParseResult(parseInputStream(file.inputStream()), file.path)

    /**
     * Parse list of files.
     * @param files files to parse
     * @param handleResult handler to invoke on each file parse result
     */
    fun parseFiles(files: List<File>, handleResult: (ParseResult<T>) -> Any) {
        files.forEach { handleResult(parseFile(it)) }
    }
}

data class ParseResult<T : Node>(val root: T?, val filePath: String)
