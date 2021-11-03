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
    override val parameters: List<FunctionInfoParameter>? = extractWithLogger(logger) { collectParameters() }

    override val modifiers: List<String> = root
        .children
        .filter { it.typeLabel == "Modifier" }
        .mapNotNull { it.token.original }

    override val annotations: List<String> = root
        .children
        .filter { it.typeLabel == "MarkerAnnotation" }
        .mapNotNull { it.children.first().token.original }

    override val isConstructor: Boolean = enclosingElement?.name?.equals(name) ?: false

    override val body: GumTreeNode? = root.children.find { it.typeLabel == "Block" }

    private fun collectEnclosingClass(): EnclosingElement<GumTreeNode>? = extractWithLogger(logger) {
        val enclosingNode = getEnclosingClassNode(root.parent) ?: return@extractWithLogger null
        val name = enclosingNode.getChildOfType(TypeLabels.simpleName)?.token?.original
        val type = when (enclosingNode.typeLabel) {
            TypeLabels.typeDeclaration -> EnclosingElementType.Class
            TypeLabels.enumDeclaration -> EnclosingElementType.Enum
            else -> error("No enclosing element type found for ${enclosingNode.typeLabel}")
        }
        EnclosingElement(type, name, enclosingNode)
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
        getChildOfType(TypeLabels.simpleName)?.token?.original ?: error("No name found for element")

    private fun GumTreeNode.getElementType(): String? = children.firstOrNull { it.isTypeNode() }?.preOrder()
        ?.mapNotNull { if (it.typeLabel == TypeLabels.arrayDimensions) "[]" else it.token.original }
        ?.joinToString(separator = "")

    private fun GumTreeNode.isTypeNode() = typeLabel.endsWith("Type")

    companion object {
        private object TypeLabels {
            const val arrayType = "ArrayType"
            const val arrayDimensions = "Dimension"
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
