package astminer.featureextraction

import astminer.common.model.Node

class PrettyNode(override val typeLabel: String, originalToken: String) : Node(originalToken) {
    override var children: MutableList<PrettyNode> = ArrayList()
    override var parent: PrettyNode? = null
        set(value) {
            value?.addChild(this)
            field = value
        }

    fun addChild(node: PrettyNode) = children.add(node)

    fun toPrettyString(indent: Int = 0, indentSymbol: String = "--"): String = with(StringBuilder()) {
        repeat(indent) { append(indentSymbol) }
        append(typeLabel)
        if (token.isNotEmpty()) {
            appendLine(" : $token")
        } else {
            appendLine()
        }
        children.forEach { append(it.toPrettyString(indent + 1, indentSymbol)) }
        toString()
    }

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }
}

fun restoreFromPrettyPrint(prettyPrintedTree: String, indentSymbol: String = "--"): PrettyNode {
    val lastNodeByIndent = HashMap<Int, PrettyNode>()
    val tree = prettyPrintedTree.lines().map { s ->
        val (node, indent) = restorePrintedNode(s, indentSymbol)
        lastNodeByIndent[indent] = node
        node.parent = lastNodeByIndent[indent - 1]
        node
    }
    return tree.first()
}

fun restorePrintedNode(printedNode: String, indentSymbol: String = "--"): Pair<PrettyNode, Int> {
    val indents = Regex("^($indentSymbol)*").find(printedNode)?.value ?: ""
    val nodeString = printedNode.substringAfter(indents)
    val type = nodeString.substringBefore(" : ")
    val token = nodeString.substringAfter(" : ", "")
    val indent = indents.length / indentSymbol.length
    return PrettyNode(type, token) to indent
}
