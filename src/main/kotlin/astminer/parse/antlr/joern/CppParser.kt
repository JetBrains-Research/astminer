package astminer.parse.antlr.joern

import java.io.File

fun parseJoernAst(nodesFile: File, edgesFile: File): JoernNode? {
    val nodesRaw = nodesFile.readLines()
    val nodesByIndex = nodesRaw.subList(1, nodesRaw.size).map { line ->
        parseNodeLine(line)
    }.toMap()

    val edgesRaw = edgesFile.readLines()
    if (edgesRaw.size == 1) {
        return null
    }
    edgesRaw.subList(1, edgesRaw.size).forEach { line ->
        parseEdgeLine(line)?.let { edge ->
            val parentNode = nodesByIndex[edge.first]
            val childNode = nodesByIndex[edge.second]
            if (parentNode != null && childNode != null) {
                parentNode.addChild(childNode)
            } else {
                throw NullPointerException("Edge references missing nodes at indices ${edge.first}, ${edge.second}")
            }
        }
    }
    for (node in nodesByIndex.values) {
        if (node.getTypeLabel() == "File") {
            return node
        }
    }
    return null
}

private fun parseNodeLine(line: String): Pair<Int, JoernNode> {
    val values = line.split('\t')
    val key = values[1].toInt()
    val nodeType = values[2]
    val code = values[3]
    return key to JoernNode(nodeType, code)
}

private fun parseEdgeLine(line: String): Pair<Int, Int>? {
    val values = line.split('\t')
    val start = values[0].toInt()
    val end = values[1].toInt()
    val typeLabel = values[2]
    if (listOf("IS_AST_PARENT", "IS_FUNCTION_OF_AST", "IS_FILE_OF").contains(typeLabel)) {
        return start to end
    } else {
        return null
    }
}
