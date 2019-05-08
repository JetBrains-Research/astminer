package astminer.parse.antlr.joern

import astminer.common.Node

class JoernNode(private val typeLabel: String, private val token: String?) : Node {
    private val metadata: MutableMap<String, Any> = HashMap()
    private var parent: Node? = null
    private var children: MutableList<Node> = mutableListOf()

    fun addChild(node: JoernNode) {
        children.add(node)
        node.setParent(this)
    }

    override fun getTypeLabel(): String {
        return typeLabel
    }

    override fun getChildren(): List<Node> {
        return children
    }

    override fun getParent(): Node? {
        return parent
    }

    override fun getToken(): String {
        return token ?: "null"
    }

    override fun isLeaf(): Boolean {
        return children.isEmpty()
    }

    override fun getMetadata(key: String): Any? {
        return metadata[key]
    }

    override fun setMetadata(key: String, value: Any) {
        metadata[key] = value
    }

    private fun setParent(node: Node) {
        parent = node
    }
}