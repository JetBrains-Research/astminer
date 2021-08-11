package astminer.parse.javaparser

import astminer.common.model.Node
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.UnaryExpr
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
            try {
                JavaParserNode(subTree, this)
            } catch (e: MysteriousNodeException) {
                logger.warn(e.message)
                null
            }
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

private fun getJavaParserNodeToken(jpNode: JPNode): String? {
    return if (jpNode.childNodes.size == 0) {
        try {
            jpNode.tokenRange.get().toString()
        } catch (e: NoSuchElementException) {
            throw MysteriousNodeException(e)
        }
    } else {
        null
    }
}

/**
 * Sometimes javaParser generates absolutely empty leaves without any information
 * which confuses the parse wrapper. This exception being thrown when such situation
 * occurs to ignore blank node.
 * */
class MysteriousNodeException(oldException: Exception) : Exception() {
    override val message: String = "Blank node generated: ${oldException.message}"
}
