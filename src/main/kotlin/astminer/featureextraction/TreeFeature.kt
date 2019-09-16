package astminer.featureextraction

import astminer.common.model.Node
import astminer.common.preOrder

/**
 * Interface that describes tree feature.
 * @param T tree feature's type
 */
interface TreeFeature<out T> {
    /**
     * Computes this feature for given tree.
     * @param tree tree for which this feature is computed
     * @return computed feature
     */
    fun compute(tree: Node) : T
}

/**
 * Tree feature for computing depth of a given tree.
 */
object Depth : TreeFeature<Int> {
    override fun compute(tree: Node): Int {
        val max =  tree.getChildren().map { compute(it) }.max() ?: 0
        return max + 1
    }
}

/**
 * Tree feature for computing branching factor of a given tree.
 */
object BranchingFactor : TreeFeature<Double> {
    override fun compute(tree: Node): Double {
        if (tree.isLeaf()) {
            return 0.0
        }
        val isLeafByNodes = tree.preOrder().groupBy { it.isLeaf() }
        val leavesNumber = isLeafByNodes[true]?.size ?: 0
        val branchingNodesNumber = isLeafByNodes[false]?.size ?: 0
        val edgesNumber = (branchingNodesNumber + leavesNumber - 1).toDouble()

        return edgesNumber / branchingNodesNumber
    }
}

/**
 * Tree feature for computing the number of nodes in a given tree.
 */
object NumberOfNodes : TreeFeature<Int> {
    override fun compute(tree: Node): Int {
        return tree.getChildren().map { compute(it) }.sum() + 1
    }
}

/**
 * Tree feature for computing list of all node tokens from a given tree.
 */
object Tokens : TreeFeature<List<String>> {
    override fun compute(tree: Node): List<String> {
        return findTokens(tree, ArrayList())
    }

    private fun findTokens(node: Node, tokensList: MutableList<String>): List<String> {
        node.getChildren().forEach { findTokens(it, tokensList) }
        tokensList.add(node.getToken())
        return tokensList
    }
}

/**
 * Tree feature for computing list of all node types from a given tree.
 */
object NodeTypes : TreeFeature<List<String>> {
    override fun compute(tree: Node): List<String> {
        return findNodeTypes(tree, ArrayList())
    }

    private fun findNodeTypes(node: Node, nodeTypesList: MutableList<String>): List<String> {
        node.getChildren().forEach { findNodeTypes(it, nodeTypesList) }
        nodeTypesList.add(node.getTypeLabel())
        return nodeTypesList
    }
}

/**
 * Tree feature for computing list of all compressible path lengths in a given tree.
 * A path is called compressible if it consists of consistent nodes that have only one child.
 */
object CompressiblePathLengths : TreeFeature<List<Int>> {
    override fun compute(tree: Node): List<Int> {
        val pathLengths = ArrayList<Int>()
        tree.preOrder().filter { it.isStartingNode() }.forEach { pathLengths.add(findPathLengthFromStartingNode(it)) }
        return pathLengths
    }

    private fun Node.isStartingNode() : Boolean {
        return this.hasOneChild() && !(this.getParent()?.hasOneChild() ?: false)
    }

    private fun Node.hasOneChild() : Boolean = getChildren().size == 1

    private fun findPathLengthFromStartingNode(node: Node) : Int {
        var length = 1
        var next = node.getChildren().first()

        while (next.hasOneChild()) {
            length++
            next = next.getChildren().first()
        }
        return length
    }
}