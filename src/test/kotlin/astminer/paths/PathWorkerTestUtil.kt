package astminer.paths

import astminer.common.model.ASTPath
import astminer.common.model.Node
import astminer.common.postOrder
import astminer.parse.antlr.SimpleNode
import org.junit.Assert

fun simpleNode(number: Int, parent: Node?): SimpleNode {
    return SimpleNode("$number", parent, "node_$number")
}

fun simpleNodes(numbers: List<Int>, parent: Node?): List<SimpleNode> {
    return numbers.map { simpleNode(it, parent) }
}

fun getPathsCountWithNoHeightLimit(leavesCount: Int, maxWidth: Int): Int {
    if (maxWidth >= leavesCount) return (leavesCount * (leavesCount - 1)) / 2
    return (leavesCount - maxWidth) * maxWidth + (maxWidth * (maxWidth - 1)) / 2
}

fun countPossiblePaths(rootNode: Node, maxHeight: Int, maxWidth: Int): Int {
    val allLeaves = rootNode.postOrder().filter { it.isLeaf() }
    val leaveOrders = allLeaves.mapIndexed { index, node -> Pair(node, index) }.toMap()

    fun Node.retrieveParentsUpToMaxHeight(maxHeight: Int): List<Node> {
        val parents: MutableList<Node> = ArrayList()
        var currentNode = this.getParent()
        while (currentNode != null && parents.size < maxHeight) {
            parents.add(currentNode)
            currentNode = currentNode.getParent()
        }
        return parents
    }

    fun Node.countPathsInSubtreeStartingFrom(startNode: Node, maxWidth: Int, topNode: Node): Int {
        val branchIndices: MutableMap<Node, Int> = HashMap()
        this.getChildren().forEachIndexed { index, node ->
            val childSubTreeLeaves = node.postOrder().filter { it.isLeaf() }
            childSubTreeLeaves.forEach { branchIndices[it] = index }
        }

        val startNodeOrder = leaveOrders[startNode]!!
        val startNodeBranchIndex = branchIndices[startNode]!!

        fun getDepth(node: Node, relativeTo: Node): Int {
            var currentNode: Node? = node
            var depth = 0
            while (currentNode != null) {
                if (currentNode === relativeTo) return depth
                currentNode = currentNode!!.getParent()
                depth++
            }
            return -1
        }

        val possibleEndNodes = this.postOrder().filter {
            it.isLeaf()
                    && getDepth(it, topNode) in (1..maxHeight)
                    && (branchIndices[it]!! > startNodeBranchIndex)
                    && (leaveOrders[it]!! > startNodeOrder)
                    && (leaveOrders[it]!! - startNodeOrder) <= maxWidth
        }
        return possibleEndNodes.size
    }

    var totalPaths = 0

    allLeaves.forEach { leaf ->
        val possibleTopNodes = leaf.retrieveParentsUpToMaxHeight(maxHeight)
        possibleTopNodes.forEach { topNode ->
            totalPaths += topNode.countPathsInSubtreeStartingFrom(leaf, maxWidth, topNode)
        }
    }

    return totalPaths
}

fun ASTPath.allNodesAreDistinct(): Boolean {
    return this.upwardNodes.size == this.upwardNodes.toSet().size
            && this.downwardNodes.size == this.downwardNodes.toSet().size
}

fun ASTPath.isSimple(): Boolean {
    return this.upwardNodes.toSet().intersect(this.downwardNodes.toSet()).size == 1
}

fun ASTPath.piecesMatch(): Boolean = this.upwardNodes.last() === this.downwardNodes.first()

fun assertPathIsValid(path: ASTPath) {
    Assert.assertTrue("Nodes in each of the path pieces should be distinct", path.allNodesAreDistinct())
    Assert.assertTrue("The pieces of the path should match on the top node", path.piecesMatch())
    Assert.assertTrue("Path should be simple: upward and downward pieces " +
            "should only have the top node in common",
            path.isSimple())
}