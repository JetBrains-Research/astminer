package astminer.common.model

import astminer.cli.LabeledResult
import java.io.File
import java.io.InputStream


abstract class Node{
    abstract val typeLabel: String
    abstract val children: List<Node>
    abstract val parent: Node?
    abstract val token: String

    val metadata: MutableMap<String, Any> = HashMap()
    fun isLeaf() = children.isEmpty()

    fun prettyPrint(indent: Int = 0, indentSymbol: String = "--") {
        repeat(indent) { print(indentSymbol) }
        print(typeLabel)
        if (token.isNotEmpty()) {
            println(" : $token")
        } else {
            println()
        }
        children.forEach { it.prettyPrint(indent + 1, indentSymbol) }
    }

    open fun getChildrenOfType(typeLabel: String) = children.filter { it.typeLabel == typeLabel }
    open fun getChildOfType(typeLabel: String) = getChildrenOfType(typeLabel).firstOrNull()

    abstract fun removeChildrenOfType(typeLabel: String)
    //TODO(move orders here)
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
    fun parseFiles(files: List<File>, handleResult: (ParseResult<T>) -> Any?) {
        files.forEach { handleResult(parseFile(it)) }
    }
}

data class ParseResult<T : Node>(val root: T?, val filePath: String) {
    fun labeledWith(label: String): LabeledResult<T>? = root?.let { LabeledResult(it, label, filePath) }

    fun labeledWithFilePath(): LabeledResult<T>? = labeledWith(filePath)
}
