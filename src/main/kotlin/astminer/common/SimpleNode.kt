package astminer.common

import astminer.common.model.Node
import astminer.common.model.NodeRange

/** Node simplest implementation **/
class SimpleNode(
    override val typeLabel: String,
    override val children: MutableList<SimpleNode>,
    override val parent: Node? = null,
    override val range: NodeRange? = null,
    token: String?
) : Node(token) {
    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun getChildrenOfType(typeLabel: String) = super.getChildrenOfType(typeLabel).map { it as SimpleNode }
    override fun getChildOfType(typeLabel: String) = super.getChildOfType(typeLabel) as? SimpleNode

    override fun preOrder() = super.preOrder().map { it as SimpleNode }
    override fun postOrder() = super.postOrder().map { it as SimpleNode }
}
