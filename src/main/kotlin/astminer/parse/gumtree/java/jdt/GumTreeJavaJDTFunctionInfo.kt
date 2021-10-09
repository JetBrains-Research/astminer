package astminer.parse.gumtree.java.jdt

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.gumtree.GumTreeNode
import mu.KotlinLogging

private val logger = KotlinLogging.logger("Gumtree-Java-function-info")

class GumTreeJavaJDTFunctionInfo(
    override val root: GumTreeNode,
    override val filePath: String
) : FunctionInfo<GumTreeNode> {

    override val nameNode: GumTreeNode? = root.getChildOfType(TypeLabels.simpleName)
    override val returnType: String? = root.getElementType()
    override val enclosingElement: EnclosingElement<GumTreeNode>? = collectEnclosingClass()
    override val parameters: List<FunctionInfoParameter>? =
        try { collectParameters() } catch (e: IllegalStateException) {
            logger.warn { e.message }
            null
        }

    override val modifiers: List<String> = root
        .children
        .filter { it.typeLabel == "Modifier" }
        .mapNotNull { it.originalToken }

    override val annotations: List<String> = root
        .children
        .filter { it.typeLabel == "MarkerAnnotation" }
        .mapNotNull { it.children.first().originalToken }

    override val isConstructor: Boolean = enclosingElement?.name?.equals(name) ?: false

    override val body: GumTreeNode? = root.children.find { it.typeLabel == "Block" }

    private fun collectEnclosingClass(): EnclosingElement<GumTreeNode>? {
        val enclosingNode = getEnclosingClassNode(root.parent) ?: return null
        val name = enclosingNode.getChildOfType(TypeLabels.simpleName)?.originalToken
        val type = when(enclosingNode.typeLabel) {
            TypeLabels.typeDeclaration -> EnclosingElementType.Class
            TypeLabels.enumDeclaration -> EnclosingElementType.Enum
            else -> error("No enclosing element type found for ${enclosingNode.typeLabel}")
        }
        return EnclosingElement(type, name, enclosingNode)
    }

    private fun getEnclosingClassNode(node: GumTreeNode?): GumTreeNode? {
        if (node == null || node.typeLabel in TypeLabels.possibleEnclosingElements) {
            return node
        }
        return getEnclosingClassNode(node.parent)
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        val params = root.getChildrenOfType(TypeLabels.singleVariableDeclaration)
        return params.map { node ->
            FunctionInfoParameter(node.getElementName(), node.getElementType())
        }
    }

    private fun GumTreeNode.getElementName(): String =
        getChildOfType(TypeLabels.simpleName)?.originalToken ?: error("No name found for element")

    private fun GumTreeNode.getElementType(): String? = children.firstOrNull { it.isTypeNode() }?.originalToken

    private fun GumTreeNode.isTypeNode() = typeLabel.endsWith("Type")

    companion object {
        private object TypeLabels {
            const val simpleName = "SimpleName"
            const val typeDeclaration = "TypeDeclaration"
            const val enumDeclaration = "EnumDeclaration"
            val possibleEnclosingElements = listOf(
                typeDeclaration,
                enumDeclaration
            )
            const val singleVariableDeclaration = "SingleVariableDeclaration"
        }
    }
}
