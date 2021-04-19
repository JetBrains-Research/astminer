package astminer.parse.antlr

import astminer.common.model.Node

class AntlrNode(private val typeLabel: String, private var parent: Node?, private var token: String?) : Node {
    override val metadata: MutableMap<String, Any> = HashMap()

    private var children: MutableList<AntlrNode> = mutableListOf()

    fun setChildren(newChildren: List<AntlrNode>) {
        children = newChildren.toMutableList()
        children.forEach { it.setParent(this) }
    }

    private fun setParent(newParent: Node?) {
        parent = newParent
    }

    override fun getTypeLabel(): String {
        return typeLabel
    }

    override fun getChildren(): List<AntlrNode> {
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

    override fun getChildrenOfType(typeLabel: String) = getChildren().filter {
        decompressTypeLabel(it.getTypeLabel()).firstOrNull() == typeLabel
    }

    override fun getChildOfType(typeLabel: String): AntlrNode? =
        getChildrenOfType(typeLabel).firstOrNull()

    override fun removeChildrenOfType(typeLabel: String) {
       children.removeIf { it.getTypeLabel() == typeLabel }
    }

}
