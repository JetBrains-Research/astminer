package astminer.parse.cpp

import astminer.common.model.Node
import com.google.common.collect.TreeMultiset

/**
 * Node for AST, created by fuzzyc2cpg.
 * @param typeLabel - node's label
 * @param token - node's token
 * @param order - node's order, which used to express the ordering of children in the AST when it matters
 */
class FuzzyNode(private val typeLabel: String, private val token: String?, order: Int?) : Node {
    private val order = order ?: -1
    private val metadata: MutableMap<String, Any> = HashMap()
    private var parent: Node? = null
    private var children = TreeMultiset.create<FuzzyNode>(compareBy(
            { it.order },
            { System.identityHashCode(it) }
    ))

    fun getOrder(): Int {
        return order
    }

    fun addChild(node: FuzzyNode) {
        children.add(node)
        node.setParent(this)
    }

    override fun getTypeLabel(): String {
        return typeLabel
    }

    override fun getChildren(): List<Node> {
        return children.toList()
    }

    override fun getParent(): Node? {
        return parent
    }

    override fun getToken(): String {
        return token ?: "null"
    }

    override fun isLeaf(): Boolean {
        return children.isEmpty()
    }

    override fun getMetadata(key: String): Any? {
        return metadata[key]
    }

    override fun setMetadata(key: String, value: Any) {
        metadata[key] = value
    }

    private fun setParent(node: Node) {
        parent = node
    }

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.getTypeLabel() == typeLabel }
    }
}
