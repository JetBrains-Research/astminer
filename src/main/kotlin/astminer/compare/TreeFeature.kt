package astminer.compare

import astminer.common.Node
import astminer.common.preOrderIterator
import java.util.*
import kotlin.collections.ArrayList


interface TreeFeature<T> {
    fun find(tree: Node) : T

    fun findAsString(tree: Node) : String {
        return find(tree).toString()
    }
}

interface TreeFeatureList<T> : TreeFeature<List<T>> {
    override fun findAsString(tree: Node): String {
        return "\"${find(tree).joinToString().replace("\"","\"\"")}\""
    }
}


object Depth : TreeFeature<Int> {
    override fun find(tree: Node): Int {
        return tree.findDepth()
    }

    private fun Node.findDepth() : Int {
        var max =  this.getChildren().map { it.findDepth() }.max() ?: 0
        return ++max
    }
}

object BranchingFactor : TreeFeature<Double> {
    override fun find(tree: Node): Double {
        if (tree.isLeaf()) return 0.0

        return findBranchingFactorsCount(tree, ArrayList()).average()
    }

    private fun findBranchingFactorsCount(node: Node, factorList: MutableList<Int>): List<Int> {
        if (node.isLeaf()) return factorList

        node.getChildren().forEach { findBranchingFactorsCount(it, factorList) }
        val factor = node.getChildren().size
        factorList.add(factor)
        return factorList
    }
}

object NumberOfNodes : TreeFeature<Int> {
    override fun find(tree: Node): Int {
        return tree.findNumberOfNodes()
    }

    private fun Node.findNumberOfNodes(): Int {
        return this.getChildren().map { it.findNumberOfNodes() }.sum() + 1
    }
}

object Tokens : TreeFeatureList<String> {
    override fun find(tree: Node): List<String> {
        return findTokens(tree, ArrayList())
    }

    private fun findTokens(node: Node, tokensList: MutableList<String>): List<String> {
        node.getChildren().forEach { findTokens(it, tokensList) }
        tokensList.add(node.getToken())
        return tokensList
    }
}

object NodeTypes : TreeFeatureList<String> {
    override fun find(tree: Node): List<String> {
        return findNodeTypes(tree, ArrayList())
    }

    private fun findNodeTypes(node: Node, nodeTypesList: MutableList<String>): List<String> {
        node.getChildren().forEach { findNodeTypes(it, nodeTypesList) }
        nodeTypesList.add(node.getTypeLabel())
        return nodeTypesList
    }
}

object DegeneratedPathLengths : TreeFeatureList<Int> {
    override fun find(tree: Node): List<Int> {
        return findDegeneratedPathLengths(tree)
    }

    private fun findDegeneratedPathLengths(tree: Node) : List<Int> {
        val visited = HashSet<Node>()
        val iterator = tree.preOrderIterator()
        val pathLengths = ArrayList<Int>()

        fun Node.hasOneChild() : Boolean = getChildren().size == 1

        iterator.forEach { n ->
            if (visited.add(n) && n.hasOneChild()) {
                var length = 1
                var next = n.getChildren().first()
                visited.add(next)

                while (next.hasOneChild()) {
                    length++
                    next = next.getChildren().first()
                    visited.add(next)
                }

                pathLengths.add(length)
            }
        }
        return pathLengths
    }

}

