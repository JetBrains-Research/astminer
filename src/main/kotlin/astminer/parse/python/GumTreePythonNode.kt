package astminer.parse.python

import astminer.common.model.Node
import com.github.gumtreediff.tree.ITree
import com.github.gumtreediff.tree.TreeContext

class GumTreePythonNode(val wrappedNode: ITree, val context: TreeContext, val parent: GumTreePythonNode?) : Node {
    private val metadata: MutableMap<String, Any> = HashMap()

    override fun getMetadata(key: String): Any? {
        return metadata[key]
    }

    override fun setMetadata(key: String, value: Any) {
        metadata[key] = value
    }

    override fun isLeaf(): Boolean {
        return childrenList.isEmpty()
    }

    private val childrenList: MutableList<GumTreePythonNode> by lazy {
        wrappedNode.children.map { GumTreePythonNode(it, context, this) }.toMutableList()
    }

    override fun getTypeLabel(): String {
        return context.getTypeLabel(wrappedNode)
    }

    override fun getChildren(): List<Node> {
        return childrenList
    }

    override fun getParent(): Node? {
        return parent
    }

    override fun getToken(): String {
        return wrappedNode.label
    }

    override fun removeChildrenOfType(typeLabel: String) {
        childrenList.removeIf { it.getTypeLabel() == typeLabel }
    }
}
