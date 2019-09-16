package astminer.common.model


data class ASTPath(val upwardNodes: List<Node>, val downwardNodes: List<Node>)

enum class Direction { UP, DOWN }

data class OrientedNodeType(val typeLabel: String, val direction: Direction)

data class PathContext(val startToken: String, val orientedNodeTypes: List<OrientedNodeType>, val endToken: String)

data class PathContextId(val startTokenId: Long, val pathId: Long, val endTokenId: Long)

data class LabeledPathContexts<T>(val label: T, val pathContexts: Collection<PathContext>)

data class LabeledPathContextIds<T>(val label: T, val pathContexts: Collection<PathContextId>)
