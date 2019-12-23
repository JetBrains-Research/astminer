package astminer.featureextraction

import astminer.common.model.Node

class PrettyNode(private val type: String, private val token: String) : Node {
    private var children: MutableList<PrettyNode> = ArrayList()
    private var parent: PrettyNode? = null
    private val metadata: MutableMap<String, Any> = HashMap()

    override fun getChildren(): MutableList<PrettyNode> =  children

    override fun getParent(): PrettyNode? = parent

    fun addChild(node: PrettyNode) = children.add(node)

    fun setParent(node: PrettyNode?) {
        node?.addChild(this)
        parent = node
    }

    fun toPrettyString(indent: Int = 0, indentSymbol: String = "--") : String = with(StringBuilder()) {
        repeat(indent) { append(indentSymbol) }
        append(getTypeLabel())
        if (getToken().isNotEmpty()) {
            appendln(" : ${getToken()}")
        } else {
            appendln()
        }
        getChildren().forEach { append(it.toPrettyString(indent + 1, indentSymbol)) }
        toString()
    }

    override fun getToken(): String =  token

    override fun isLeaf(): Boolean =  children.isEmpty()

    override fun getMetadata(key: String): Any? = metadata[key]

    override fun setMetadata(key: String, value: Any) = metadata.set(key, value)

    override fun getTypeLabel(): String = type

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.getTypeLabel() == typeLabel }
    }

}

fun restoreFromPrettyPrint(prettyPrintedTree: String, indentSymbol: String = "--") : PrettyNode? {
    val lastNodeByIndent = HashMap<Int, PrettyNode>()
    val tree = prettyPrintedTree.lines().map { s ->
        val (node, indent) = restorePrintedNode(s, indentSymbol)
        lastNodeByIndent[indent] = node
        node.setParent(lastNodeByIndent[indent - 1])
        node
    }
    return tree.first()
}


fun restorePrintedNode(printedNode: String, indentSymbol: String = "--") : Pair<PrettyNode, Int> {
    val indents = Regex("^($indentSymbol)*").find(printedNode)?.value ?: ""
    val nodeString = printedNode.substringAfter(indents)
    val type =  nodeString.substringBefore(" : ")
    val token = nodeString.substringAfter(" : ", "")
    val indent = indents.length / indentSymbol.length
    return PrettyNode(type, token) to indent
}
