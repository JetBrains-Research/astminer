package astminer.paths

import astminer.common.model.ASTPath
import astminer.common.model.Node
import astminer.common.model.PathPiece
import astminer.common.postOrderIterator
import kotlin.math.min

class PathWorker {

    companion object {
        private const val PATH_PIECES_KEY = "path_pieces"

        private fun Node.setPathPieces(pathPieces: List<PathPiece>) {
            this.setMetadata(PATH_PIECES_KEY, pathPieces)
        }

        private fun Node.getPathPieces(): List<PathPiece>? = this.getMetadata(PATH_PIECES_KEY) as List<PathPiece>?
    }

    fun retrievePaths(tree: Node) = retrievePaths(tree, null, null)

    fun updatePathPieces(
            currentNode: Node,
            pathPiecesPerChild: List<List<PathPiece>?>,
            maxLength: Int?
    ) = pathPiecesPerChild.filterNotNull().flatMap { childPieces ->
        childPieces.filter { pathPiece ->
            maxLength == null || pathPiece.size <= maxLength
        }.map { pathPiece ->
            pathPiece + currentNode
        }
    }

    fun collapsePiecesToPaths(
            currentNode: Node,
            pathPiecesPerChild: List<List<PathPiece>?>,
            maxLength: Int?, maxWidth: Int?
    ): Collection<ASTPath> {
        val paths: MutableCollection<ASTPath> = ArrayList()
        val childrenCount = pathPiecesPerChild.size
        pathPiecesPerChild.forEachIndexed { index, leftChildPieces ->
            val maxIndex = maxWidth?.let { min(index + maxWidth + 1, childrenCount) } ?: childrenCount
            pathPiecesPerChild.subList(index + 1, maxIndex).forEach { rightChildPieces ->
                leftChildPieces?.forEach { upPiece ->
                    rightChildPieces?.forEach { downPiece ->
                        if (maxLength == null || upPiece.size + 1 + downPiece.size <= maxLength) {
                            paths.add(ASTPath(upPiece, currentNode, downPiece.asReversed()))
                        }
                    }
                }
            }
        }
        return paths
    }

    fun retrievePaths(tree: Node, maxLength: Int?, maxWidth: Int?): Collection<ASTPath> {
        val iterator = tree.postOrderIterator()
        val paths: MutableList<ASTPath> = ArrayList()
        iterator.forEach { currentNode ->
            if (currentNode.isLeaf()) {
                if (currentNode.getToken().isNotEmpty()) {
                    currentNode.setPathPieces(listOf(listOf(currentNode)))
                }
            } else {
                val pathPiecesPerChild = currentNode.getChildren().map { it.getPathPieces() }
                val currentNodePathPieces = updatePathPieces(currentNode, pathPiecesPerChild, maxLength)
                val currentNodePaths = collapsePiecesToPaths(currentNode, pathPiecesPerChild, maxLength, maxWidth)

                paths.addAll(currentNodePaths)
                currentNode.setPathPieces(currentNodePathPieces)
            }
        }
        return paths
    }
}