package astminer.parse.fuzzy

import astminer.common.model.Node
import com.google.common.collect.TreeMultiset

/**
 * Node for AST, created by fuzzyc2cpg.
 * @param typeLabel - node's label
 * @param originalToken - node's token
 * @param order - node's order, which used to express the ordering of children in the AST when it matters
 */
class FuzzyNode(
    override val typeLabel: String,
    originalToken: String?,
    order: Int?
) : Node(originalToken) {
    private val order = order ?: -1
    override var parent: Node? = null
    private val childrenMultiset = TreeMultiset.create<FuzzyNode>(
        compareBy({ it.order }, { System.identityHashCode(it) })
    )

    override val children
        get() = childrenMultiset.toList()

    fun addChild(node: FuzzyNode) {
        childrenMultiset.add(node)
        node.parent = this
    }

    override fun removeChildrenOfType(typeLabel: String) {
        childrenMultiset.removeIf { it.typeLabel == typeLabel }
    }

    override fun preOrder(): List<FuzzyNode> = super.preOrder().map { it as FuzzyNode }
}
