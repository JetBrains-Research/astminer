package miningtool.common

fun toPathContext(path: ASTPath): PathContext {
    val startToken = path.upwardNodes.first().getToken()
    val endToken = path.downwardNodes.last().getToken()
    val astNodes = path.upwardNodes
            .takeLast(path.upwardNodes.size - 1).map { NodeType(it.getTypeLabel(), Direction.UP) } +
            path.downwardNodes.take(path.downwardNodes.size - 1).map { NodeType(it.getTypeLabel(), Direction.DOWN) }
    return PathContext(startToken, astNodes, endToken)
}