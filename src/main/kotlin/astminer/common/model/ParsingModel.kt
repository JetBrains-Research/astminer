package astminer.common.model

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

typealias Line = Int
typealias Column = Int

data class NodeRange(val start: Pair<Line, Column>, val end: Pair<Line, Column>)

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
