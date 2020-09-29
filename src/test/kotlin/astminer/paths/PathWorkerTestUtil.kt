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

fun getParentStack(node: Node): List<Node> = (node.getParent()?.let { getParentStack(it) } ?: emptyList()) + node

fun getAllPathCharacteristics(root: Node): Collection<Pair<Int, Int>> {
    val leaves = root.postOrder().filter { it.isLeaf() }
    val allPathCharacteristics = mutableListOf<Pair<Int, Int>>()
    leaves.forEachIndexed { indexLeft, leafLeft ->
        leaves.forEachIndexed { indexRight, leafRight ->
            if (indexLeft < indexRight) {
                val leftStack = getParentStack(leafLeft)
                val rightStack = getParentStack(leafRight)
                var leftDepth = leftStack.size
                var rightDepth = rightStack.size
                leftStack.zip(rightStack).zipWithNext { (left1, right1), (left2, right2) ->
                    if (left1 == right1 && left2 != right2) {
                        val leftIndex = left1.getChildren().indexOf(left2)
                        val rightIndex = left1.getChildren().indexOf(right2)
                        allPathCharacteristics.add(Pair(rightIndex - leftIndex, leftDepth + rightDepth - 1))
                        return@zipWithNext
                    }
                    leftDepth--
                    rightDepth--
                }
            }
        }
    }
    return allPathCharacteristics
}

fun ASTPath.allNodesAreDistinct(): Boolean {
    return this.upwardNodes.size == this.upwardNodes.toSet().size
            && this.downwardNodes.size == this.downwardNodes.toSet().size
}

fun ASTPath.isSimple(): Boolean {
    return this.upwardNodes.toSet().intersect(this.downwardNodes.toSet()).isEmpty()
            && !this.upwardNodes.contains(this.topNode)
            && !this.downwardNodes.contains(this.topNode)
}

fun ASTPath.piecesMatch(): Boolean = this.upwardNodes.last() === this.downwardNodes.first()

fun assertPathIsValid(path: ASTPath) {
    Assert.assertTrue("Nodes in each of the path pieces should be distinct", path.allNodesAreDistinct())
    Assert.assertTrue(
            "Path should be simple: upward and downward pieces should not intersect or contain top node",
            path.isSimple()
    )
}