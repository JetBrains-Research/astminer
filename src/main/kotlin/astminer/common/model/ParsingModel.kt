package astminer.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.File
import java.io.InputStream

abstract class Node(originalToken: String?) {
    abstract val typeLabel: String
    abstract val children: List<Node>
    abstract val parent: Node?
    abstract val range: NodeRange?
    val metadata: MutableMap<String, Any> = HashMap()
    val token = Token(originalToken)

    fun isLeaf() = children.isEmpty()

    override fun toString(): String = "$typeLabel : $token"

    fun prettyPrint(indent: Int = 0, indentSymbol: String = "--") {
        repeat(indent) { print(indentSymbol) }
        println(this)
        children.forEach { it.prettyPrint(indent + 1, indentSymbol) }
    }

    open fun getChildrenOfType(typeLabel: String) = children.filter { it.typeLabel == typeLabel }
    open fun getChildOfType(typeLabel: String) = getChildrenOfType(typeLabel).firstOrNull()

    abstract fun removeChildrenOfType(typeLabel: String)

    private fun doTraversePreOrder(resultList: MutableList<Node>) {
        resultList.add(this)
        children.forEach { it.doTraversePreOrder(resultList) }
    }

    fun preOrderIterator(): Iterator<Node> = preOrder().listIterator()
    open fun preOrder(): List<Node> = mutableListOf<Node>().also { doTraversePreOrder(it) }

    private fun doTraversePostOrder(resultList: MutableList<Node>) {
        children.forEach { it.doTraversePostOrder(resultList) }
        resultList.add(this)
    }

    fun postOrderIterator(): Iterator<Node> = postOrder().listIterator()
    open fun postOrder(): List<Node> = mutableListOf<Node>().also { doTraversePostOrder(it) }
}

@Serializable
data class NodeRange(val start: Position, val end: Position) {
    override fun toString(): String = "[${start.line}, ${start.column}] - [${end.line}, ${end.column}]"
}

@Serializable
data class Position(@SerialName("l") val line: Int, @SerialName("c") val column: Int)

interface Parser<T : Node> {
    /**
     * Parse input stream into an AST.
     * @param content input stream to parse
     * @return root of the AST
     */
    fun parseInputStream(content: InputStream): T

    /**
     * Parse file into an AST.
     * @param file file to parse
     * @return ParseResult instance
     */
    fun parseFile(file: File) = parseInputStream(file.inputStream())
}

class ParserNotInstalledException(parser: String, language: String, val e: Exception) : Exception() {
    override val message: String = "Tools for parsing $language with $parser were not properly installed"
    override val cause: Throwable = e
}
