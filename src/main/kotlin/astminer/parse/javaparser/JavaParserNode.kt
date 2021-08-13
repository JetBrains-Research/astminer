package astminer.parse.javaparser

import astminer.common.model.Node
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.UnaryExpr
import com.github.javaparser.ast.type.UnknownType
import mu.KotlinLogging
import java.util.NoSuchElementException
import com.github.javaparser.ast.Node as JPNode

private val logger = KotlinLogging.logger("JavaParser-Node")

/**
 * Representation of JavaParser nodes inside `astminer`
 *
 * @property jpNode node from javapParser. JPNode is an alias for Node from javaparser
 * @property parent parent of this node. Null if it's a root.
 */
class JavaParserNode(jpNode: JPNode, override val parent: JavaParserNode?) : Node(getJavaParserNodeToken(jpNode)) {
    override val children: MutableList<JavaParserNode> =
        jpNode.childNodes.mapNotNull { subTree ->
            if (subTree.isPhantom || (subTree.isLeaf() && subTree.hasNoToken())) return@mapNotNull null
            JavaParserNode(subTree, this)
        }.toMutableList()

    /**
     * Node type label. Value is shortened if possible to preserve space.
     * */
    override val typeLabel: String = run {
        val rawType = getRawType(jpNode)
        SHORTEN_VALUES.getOrDefault(rawType, rawType)
    }

    /**
     * Returns node type. Composed of `javaClass.simpleName` and
     * `jpNode.operator` if node is expression.
     * */
    private fun getRawType(jpNode: JPNode): String {
        val type = jpNode.javaClass.simpleName
        val operator = when (jpNode) {
            is UnaryExpr -> ":${jpNode.operator}"
            is BinaryExpr -> ":${jpNode.operator}"
            is AssignExpr -> ":${jpNode.operator}"
            else -> ""
        }
        return type + operator
    }

    override fun preOrder(): List<JavaParserNode> = super.preOrder().map { it as JavaParserNode }
    override fun postOrder(): List<JavaParserNode> = super.postOrder().map { it as JavaParserNode }

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun getChildrenOfType(typeLabel: String): List<JavaParserNode> =
        super.getChildrenOfType(typeLabel).map { it as JavaParserNode }

    override fun getChildOfType(typeLabel: String): JavaParserNode? =
        super.getChildOfType(typeLabel) as? JavaParserNode
}

private fun JPNode.isLeaf() : Boolean = this.childNodes.isEmpty()

private fun JPNode.hasNoToken(): Boolean = !this.tokenRange.isPresent

private fun getJavaParserNodeToken(jpNode: JPNode): String? {
    if (jpNode.childNodes.isNotEmpty()) return null
    return jpNode.tokenRange.get().toString()
}
