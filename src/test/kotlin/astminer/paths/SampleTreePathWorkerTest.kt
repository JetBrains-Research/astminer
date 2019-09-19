package astminer.paths

import astminer.common.model.Node

class SampleTreePathWorkerTest : PathWorkerTestBase() {
    override fun getTree(): Node {
        val root = simpleNode(1, null)

        val rootChildren = simpleNodes(listOf(2, 3), root)
        val (node2, node3) = rootChildren
        root.setChildren(rootChildren)

        val node2Children = simpleNodes(listOf(4, 5), node2)
        val (_, node5) = node2Children
        node2.setChildren(node2Children)

        val node3Children = simpleNodes(listOf(6, 7, 8), node3)
        val (_, node7, _) = node3Children
        node3.setChildren(node3Children)

        val node5Children = simpleNodes(listOf(9, 10, 11), node5)
        node5.setChildren(node5Children)

        val node7Children = simpleNodes(listOf(12, 13), node7)
        node7.setChildren(node7Children)

        return root
    }
}