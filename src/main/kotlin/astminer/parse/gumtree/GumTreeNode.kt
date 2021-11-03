package astminer.parse.gumtree

import astminer.common.model.Node
import astminer.common.model.NodeRange
import com.github.gumtreediff.tree.Tree

class GumTreeNode(val wrappedNode: Tree, posConverter: PositionConverter, override var parent: GumTreeNode? = null) :
    Node(wrappedNode.label) {
    override val typeLabel: String = wrappedNode.type.name

    override val children: MutableList<GumTreeNode> by lazy {
        wrappedNode.children.map { GumTreeNode(it, posConverter, this) }.toMutableList()
    }

    override val range: NodeRange = posConverter.getRange(wrappedNode.pos, wrappedNode.endPos)

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun getChildOfType(typeLabel: String): GumTreeNode? =
        getChildrenOfType(typeLabel).firstOrNull()

    override fun getChildrenOfType(typeLabel: String): List<GumTreeNode> {
        val children = super.getChildrenOfType(typeLabel)
        return children.filterIsInstance<GumTreeNode>()
            .apply { if (size != children.size) throw TypeCastException("Node have children of different types") }
    }

    override fun preOrder(): List<GumTreeNode> = super.preOrder().map { it as GumTreeNode }
}
