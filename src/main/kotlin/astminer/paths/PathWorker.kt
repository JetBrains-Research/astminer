package astminer.paths

import astminer.common.model.ASTPath
import astminer.common.model.Node
import astminer.common.postOrderIterator

class PathWorker {

    companion object {
        private const val LEAF_INDEX_KEY = "leaf_index"
        private const val PATH_PIECES_KEY = "path_pieces"

        private fun Node.setLeafIndex(index: Int) {
            this.setMetadata(LEAF_INDEX_KEY, index)
        }

        private fun Node.getLeafIndex(): Int = this.getMetadata(LEAF_INDEX_KEY) as Int

        private fun Node.setPathPieces(pathPieces: List<List<Node>>) {
            this.setMetadata(PATH_PIECES_KEY, pathPieces)
        }

        private fun Node.getPathPieces(): List<List<Node>> = this.getMetadata(PATH_PIECES_KEY) as List<List<Node>>
    }

    fun retrievePaths(tree: Node) = retrievePaths(tree, Int.MAX_VALUE, Int.MAX_VALUE)

    data class PathPiece(val childIndex: Int, val nodes: List<Node>)

    fun collapsePiecesToPaths(pathPieces: Collection<PathPiece>, maxWidth: Int): Collection<ASTPath> {
        val paths: MutableCollection<ASTPath> = ArrayList()
        val sortedPieces = pathPieces.sortedBy { (it.nodes[0].getLeafIndex()) }
        sortedPieces.forEachIndexed { index, upPiece ->
            for (downPiece in sortedPieces.subList(index + 1, sortedPieces.size)) {
                if (upPiece.childIndex == downPiece.childIndex) continue

                val width = downPiece.nodes[0].getLeafIndex() - upPiece.nodes[0].getLeafIndex()
                if (width <= maxWidth) {
                    paths.add(ASTPath(upPiece.nodes, downPiece.nodes.reversed()))
                }
            }
        }
        return paths
    }

    fun retrievePaths(tree: Node, maxHeight: Int, maxWidth: Int): Collection<ASTPath> {
        val iterator = tree.postOrderIterator()
        var currentLeafIndex = 0
        val paths: MutableList<ASTPath> = ArrayList()
        iterator.forEach { currentNode ->
            if (currentNode.isLeaf()) {
                currentNode.setLeafIndex(currentLeafIndex++)
                currentNode.setPathPieces(listOf(listOf(currentNode)))
            } else {

                val pathPiecesPerChild = currentNode.getChildren()
                        .map { it.getPathPieces() }

                val pathPieces: MutableList<PathPiece> = ArrayList()

                pathPiecesPerChild.forEachIndexed { childIndex, childPieces ->
                    childPieces.forEach {
                        if (it.size <= maxHeight) {
                            pathPieces.add(PathPiece(childIndex, it + currentNode))
                        }
                    }
                }

                val currentNodePaths = collapsePiecesToPaths(pathPieces, maxWidth)
                paths.addAll(currentNodePaths)

                currentNode.setPathPieces(pathPieces.map { it.nodes })
            }
        }
        return paths
    }
}