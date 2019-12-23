package astminer.common

import astminer.common.model.Node


class DummyNode(val data: String, val childrenList: MutableList<DummyNode>) : Node {
    override fun setMetadata(key: String, value: Any) {

    }

    override fun getMetadata(key: String): Any? {
        return null
    }

    override fun isLeaf(): Boolean {
        return childrenList.isEmpty()
    }

    override fun getTypeLabel(): String {
        return data
    }

    override fun getChildren(): List<Node> {
        return childrenList
    }

    override fun getParent(): Node? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getToken(): String {
        return data
    }

    override fun removeChildrenOfType(typeLabel: String) {
        childrenList.removeIf { it.getTypeLabel() == typeLabel }
    }

}

fun createDummyTree(): DummyNode {
    val node4 = DummyNode("4", mutableListOf())
    val node5 = DummyNode("5", mutableListOf())
    val node6 = DummyNode("6", mutableListOf())
    val node7 = DummyNode("7", mutableListOf())
    val node8 = DummyNode("8", mutableListOf())

    val node2 = DummyNode("2", mutableListOf(node4, node5, node6))
    val node3 = DummyNode("3", mutableListOf(node7, node8))

    return DummyNode("1", mutableListOf(node2, node3))
}

fun createSmallTree(): DummyNode {
    val node4 = DummyNode("4", mutableListOf())
    val node3 = DummyNode("3", mutableListOf(node4))
    val node2 = DummyNode("2", mutableListOf())
    val node1 = DummyNode("1", mutableListOf(node2, node3))

    return node1
}
