package astminer.paths

import astminer.common.*

fun toPathContext(path: ASTPath, getToken: (Node) -> String = { node -> node.getNormalizedToken()}): PathContext {
    val startToken = getToken(path.upwardNodes.first())
    val endToken = getToken(path.downwardNodes.last())
    val astNodes = path.upwardNodes
            .takeLast(path.upwardNodes.size - 1).map { OrientedNodeType(it.getTypeLabel(), Direction.UP) } +
            path.downwardNodes.take(path.downwardNodes.size - 1).map { OrientedNodeType(it.getTypeLabel(), Direction.DOWN) }
    return PathContext(startToken, astNodes, endToken)
}
