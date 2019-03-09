package astminer.paths

import astminer.common.ASTPath
import astminer.common.Direction
import astminer.common.OrientedNode
import astminer.common.PathContext

fun toPathContext(path: ASTPath): PathContext {
    val startToken = path.upwardNodes.first().getToken()
    val endToken = path.downwardNodes.last().getToken()
    val astNodes = path.upwardNodes
            .takeLast(path.upwardNodes.size - 1).map { OrientedNode(it.getTypeLabel(), Direction.UP) } +
            path.downwardNodes.take(path.downwardNodes.size - 1).map { OrientedNode(it.getTypeLabel(), Direction.DOWN) }
    return PathContext(startToken, astNodes, endToken)
}