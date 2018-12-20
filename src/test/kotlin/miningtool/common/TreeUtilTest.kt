package miningtool.common

import org.junit.Assert
import org.junit.Test

class DummyNode(val data: String, val childrenList: List<DummyNode>) : Node {
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
}

fun createDummyTree(): DummyNode {
    val node4 = DummyNode("4", emptyList())
    val node5 = DummyNode("5", emptyList())
    val node6 = DummyNode("6", emptyList())
    val node7 = DummyNode("7", emptyList())
    val node8 = DummyNode("8", emptyList())

    val node2 = DummyNode("2", listOf(node4, node5, node6))
    val node3 = DummyNode("3", listOf(node7, node8))

    return DummyNode("1", listOf(node2, node3))
}

class TreeUtilTest {
    @Test
    fun testPostOrder() {
        val root = createDummyTree()
        val dataList = root.postOrderIterator().asSequence().map { it.getTypeLabel() }

        Assert.assertArrayEquals(arrayOf("4", "5", "6", "2", "7", "8", "3", "1"), dataList.toList().toTypedArray())
    }

    @Test
    fun testPreOrder() {
        val root = createDummyTree()
        val dataList = root.preOrderIterator().asSequence().map { it.getTypeLabel() }

        Assert.assertArrayEquals(arrayOf("1", "2", "4", "5", "6", "3", "7", "8"), dataList.toList().toTypedArray())
    }
}