package astminer.storage

import astminer.common.SimpleNode
import astminer.common.model.Node
import astminer.common.model.NodeRange
import kotlin.test.assertEquals

fun tree(treeConf: TreeContext.() -> Unit): SimpleNode {
    val config = TreeContext().also(treeConf)
    val new = SimpleNode(config.typeLabel, config.children, config.parent, config.range, config.originalToken)
    new.token.technical = config.technicalToken
    return new
}

class TreeContext {
    lateinit var typeLabel: String
    var originalToken: String? = null
    var technicalToken: String? = null
    var parent: SimpleNode? = null
    val children: MutableList<SimpleNode> = mutableListOf()
    val range: NodeRange? = null

    fun child(childContext: TreeContext.() -> Unit) {
        val child = tree(childContext)
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
