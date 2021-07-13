package astminer.parse.javaparser

import astminer.common.model.Node
import com.github.javaparser.ast.expr.AssignExpr
import com.github.javaparser.ast.expr.BinaryExpr
import com.github.javaparser.ast.expr.UnaryExpr
import com.github.javaparser.ast.Node as JPNode

/* Be aware that JPNode is just an alias for Node from javaparser*/
class JavaParserNode(jpNode: JPNode, override val parent: Node?) : Node() {
    override val originalToken: String? = getValue(jpNode)
    override val children: MutableList<Node> = run {
        jpNode.childNodes.map { subTree -> JavaParserNode(subTree, this) }.toMutableList()
    }
    override val typeLabel: String = run {
        val rawType = getRawType(jpNode)
        SHORTEN_VALUES.getOrDefault(rawType, rawType)
    }

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    /* For some reason code2seq JavaExtractor also checks for boxed type
       and sets its type to PrimitiveType
       which is not necessary since javaclass.simpleName
       will be PrimitiveType nonetheless*/
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

    private fun getValue(jpNode: JPNode): String? {
        return if (jpNode.childNodes.size == 0) { jpNode.tokenRange.get().toString() } else { null }
    }
}