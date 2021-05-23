package astminer.parse.antlr

import astminer.common.DEFAULT_TOKEN
import astminer.common.model.Node

class AntlrNode(override val typeLabel: String, override var parent: AntlrNode?, token: String?) : Node() {

    override val children: MutableList<AntlrNode> = mutableListOf()

    override val originalToken: String = token ?: DEFAULT_TOKEN

    fun replaceChildren(newChildren: List<AntlrNode>) {
        children.clear()
        newChildren.forEach { it.parent = this }
        children.addAll(newChildren)
    }

    override fun getChildrenOfType(typeLabel: String) = children.filter {
        decompressTypeLabel(it.typeLabel).firstOrNull() == typeLabel
    }

    override fun getChildOfType(typeLabel: String): AntlrNode? =
        getChildrenOfType(typeLabel).firstOrNull()

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun preOrder(): List<AntlrNode> = super.preOrder().map { it as AntlrNode }
}
