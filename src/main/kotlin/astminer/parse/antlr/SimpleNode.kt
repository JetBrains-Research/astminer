package astminer.parse.antlr

import astminer.common.model.Node

class SimpleNode(private val typeLabel: String, private var parent: Node?, private var token: String?) : Node {
    private val metadata: MutableMap<String, Any> = HashMap()

    private var children: MutableList<Node> = mutableListOf()

    fun setChildren(newChildren: List<Node>) {
        children = newChildren.toMutableList()
        children.forEach { (it as SimpleNode).setParent(this) }
    }

    fun setParent(newParent: Node?) {
        parent = newParent
    }

    override fun getTypeLabel(): String {
        return typeLabel
    }

    override fun getChildren(): MutableList<Node> {
        return children
    }

    override fun getParent(): Node? {
        return parent
    }

    override fun getToken(): String {
        return token ?: "null"
    }

    fun setToken(newToken: String) {
        token = newToken
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

    override fun getChildrenOfType(typeLabel: String) = getChildren().filter {
        decompressTypeLabel(it.getTypeLabel()).firstOrNull() == typeLabel
    }

    override fun removeChildrenOfType(typeLabel: String) {
       children.removeIf { it.getTypeLabel() == typeLabel }
    }

}
