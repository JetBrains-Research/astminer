package astminer.paths

import astminer.common.model.*

fun toPathContext(path: ASTPath, getToken: (Node) -> String = { node -> node.token }): PathContext {
    val startToken = getToken(path.upwardNodes.first())
    val endToken = getToken(path.downwardNodes.last())
    val astNodes = path.upwardNodes.map { OrientedNodeType(it.typeLabel, Direction.UP) } +
        OrientedNodeType(path.topNode.typeLabel, Direction.TOP) +
        path.downwardNodes.map { OrientedNodeType(it.typeLabel, Direction.DOWN) }
    return PathContext(startToken, astNodes, endToken)
}
