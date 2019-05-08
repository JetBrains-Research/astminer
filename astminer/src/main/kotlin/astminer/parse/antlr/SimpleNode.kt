package astminer.parse.antlr

import astminer.common.Node

class SimpleNode(private val typeLabel: String, private val parent: Node?, private val token: String?) : Node {
    private val metadata: MutableMap<String, Any> = HashMap()

    private var children: List<Node> = emptyList()

    fun setChildren(newChildren: List<Node>) {
        children = newChildren
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
}