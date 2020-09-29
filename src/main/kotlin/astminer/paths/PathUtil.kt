package astminer.paths

import astminer.common.model.*

fun toPathContext(path: ASTPath, getToken: (Node) -> String = { node -> node.getToken() }): PathContext {
    val startToken = getToken(path.upwardNodes.first())
    val endToken = getToken(path.downwardNodes.last())
    val astNodes = path.upwardNodes.map { OrientedNodeType(it.getTypeLabel(), Direction.UP) } +
            OrientedNodeType(path.topNode.getTypeLabel(), Direction.TOP) +
            path.downwardNodes.map { OrientedNodeType(it.getTypeLabel(), Direction.DOWN) }
    return PathContext(startToken, astNodes, endToken)
}
