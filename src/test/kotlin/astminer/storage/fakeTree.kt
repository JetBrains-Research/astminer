package astminer.storage

import astminer.common.SimpleNode
import astminer.common.model.LabeledResult
import astminer.common.model.Node
import astminer.common.model.NodeRange
import astminer.common.model.Position
import kotlin.test.assertEquals

fun tree(treeConf: TreeContext.() -> Unit): SimpleNode {
    val config = TreeContext().also(treeConf)
    return buildFromContext(config)
}

private fun buildFromContext(context: TreeContext, parent: SimpleNode? = null): SimpleNode {
    val new = SimpleNode(context.typeLabel, mutableListOf(), parent, context.range, context.originalToken)
    new.token.technical = context.technicalToken
    new.children.addAll(context.children.map { buildFromContext(it, new) })
    return new
}

class TreeContext {
    lateinit var typeLabel: String
    var originalToken: String? = null
    var technicalToken: String? = null
    val children: MutableList<TreeContext> = mutableListOf()
    var range: NodeRange? = null

    fun child(childContext: TreeContext.() -> Unit) {
        val child = TreeContext().also(childContext)
        children.add(child)
    }
}

fun assertTreesEquals(expected: Node, actual: Node) {
    assertEquals(expected.typeLabel, actual.typeLabel)
    assertEquals(expected.range, actual.range)
    assertEquals(expected.token.original, actual.token.original)
    assertEquals(expected.token.technical, actual.token.technical)
    assertEquals(expected.children.size, actual.children.size)
    expected.children.zip(actual.children).forEach { (ex, ac) -> assertTreesEquals(ex, ac) }
}

fun generateMockedResults(count: Int): List<LabeledResult<out Node>> {
    return List(count) {
        LabeledResult(
            root = tree {
                typeLabel = "mockedNode"
                range = NodeRange(
                    Position(it, it),
                    Position(it + 1, it + 1)
                )
            },
            label = "$it",
            filePath = "/$it/$it/$it"
        )
    }
}
