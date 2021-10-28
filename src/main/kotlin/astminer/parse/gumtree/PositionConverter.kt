package astminer.parse.gumtree

import astminer.common.model.NodeRange
import astminer.common.model.Position

class PositionConverter(content: String) {
    private val newLineIndexes: List<Int> =
        content.asSequence().mapIndexedNotNull { index, c -> if (c != '\n') null else index }.toList()

    private fun searchPosition(pos: Int): Position {
        val line = newLineIndexes.binarySearch(pos)
        if (line >= 0) return searchPosition(pos - 1)
        if (line == -1) return Position(1, pos)
        val previousNewLine = -line - 2
        return Position(previousNewLine + 2, pos - newLineIndexes[previousNewLine])
    }

    fun getRange(pos: Int, endPos: Int): NodeRange {
        val start = searchPosition(pos)
        val end = searchPosition(endPos)
        return NodeRange(start, end)
    }
}
