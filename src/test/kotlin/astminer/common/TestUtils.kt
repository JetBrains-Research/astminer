package astminer.common

import astminer.problem.LabeledResult
import astminer.common.model.Node
import astminer.common.model.ParseResult


class DummyNode(val data: String, val childrenList: MutableList<DummyNode> = mutableListOf()) : Node {
    override val metadata: MutableMap<String, Any> = hashMapOf()

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

/**
 * Returns a small tree.
 * Diagram:
 *         1
 *       /   \
 *     /      \
 *    2        3
 *  / | \     / \
 * 4  5  6   7   8
 *
 */
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

/**
 * Returns a small tree.
 * Diagram:
 *      1
 *    /  \
 *   2    3
 *         \
 *          4
 */
fun createSmallTree(): DummyNode {
    val node4 = DummyNode("4", mutableListOf())
    val node3 = DummyNode("3", mutableListOf(node4))
    val node2 = DummyNode("2", mutableListOf())
    val node1 = DummyNode("1", mutableListOf(node2, node3))

    return node1
}

/**
 * Creates a bamboo
 * Diagram for [size] 3:
 * 1
 *  \
 *   2
 *    \
 *     3
 */
fun createBamboo(size: Int): DummyNode {
    var root = DummyNode(size.toString(), mutableListOf())
    for (i in 1 until size) {
        root = DummyNode((size - i).toString(), mutableListOf(root))
    }
    return root
}

fun <T : Node> T.toParseResult() = ParseResult(this, "")

fun <T : Node> T.labeledWith(label: String) = LabeledResult(this, label, "")
