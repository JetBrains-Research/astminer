package astminer.parse.javaparser

import astminer.common.model.Node
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.UnaryExpr
import java.util.NoSuchElementException
import com.github.javaparser.ast.Node as JPNode

/* Be aware that JPNode is just an alias for Node from javaparser*/
class JavaParserNode(jpNode: JPNode, override val parent: Node?) : Node() {
    override val children: MutableList<JavaParserNode> = run {
        jpNode.childNodes.mapNotNull { subTree ->
            try {
                JavaParserNode(subTree, this)
            } catch (e: MysteriousNodeException) {
                logger.warn(e.message)
                null
            }
        }.toMutableList()
    }

    /* For some reason code2seq JavaExtractor also checks for boxed type
       and sets its type to PrimitiveType
       which is not necessary since javaclass.simpleName
       will be PrimitiveType nonetheless */
    override val typeLabel: String = run {
        val rawType = getRawType(jpNode)
        SHORTEN_VALUES.getOrDefault(rawType, rawType)
    }

    override val originalToken: String? = getValue(jpNode)

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

    /* Sometimes javaParser generates absolutely empty leaves without any information
       which confuses the parse wrapper. Exception being thrown when such situation
       occurs to ignore blank node. */
    private fun getValue(jpNode: JPNode): String? {
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

class MysteriousNodeException(oldException: Exception) : Exception() {
    override val message: String = "Blank node generated: ${oldException.message}"
}
