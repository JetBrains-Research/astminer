package miningtool.parse.antlr

import miningtool.common.Node

class SimpleNode(private val myTypeLabel: String, private val myParent: Node?, private val myToken: String?) : Node {
    private val myMetadata: MutableMap<String, Any> = HashMap()

    private var myChildren: List<Node> = emptyList()

    fun setChildren(newChildren: List<Node>) {
        myChildren = newChildren
    }

    override fun getTypeLabel(): String {
        return myTypeLabel
    }

    override fun getChildren(): List<Node> {
        return myChildren
    }

    override fun getParent(): Node? {
        return myParent
    }

    override fun getToken(): String {
        return myToken ?: "null"
    }

    override fun isLeaf(): Boolean {
        return myChildren.isEmpty()
    }

    override fun getMetadata(key: String): Any? {
        return myMetadata[key]
    }

    override fun setMetadata(key: String, value: Any) {
        myMetadata[key] = value
    }
}