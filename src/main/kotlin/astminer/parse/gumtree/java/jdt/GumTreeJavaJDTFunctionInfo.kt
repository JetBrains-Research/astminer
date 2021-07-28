package astminer.parse.gumtree.java.jdt

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.gumtree.GumTreeNode

class GumTreeJavaJDTFunctionInfo(
    override val root: GumTreeNode,
    override val filePath: String
) : FunctionInfo<GumTreeNode> {

    override val nameNode: GumTreeNode? = root.getChildOfType(TypeLabels.simpleName)
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val returnType: String? = root.getElementType()
    override val enclosingElement: EnclosingElement<GumTreeNode>? = collectEnclosingClass()

    override val modifiers: List<String> = root.children.filter { it.typeLabel == "Modifier" }.map { it.originalToken }
    override val annotations: List<String> = root
        .children
        .filter { it.typeLabel == "MarkerAnnotation" }
        .map { it.children.first().originalToken }
    override val isConstructor: Boolean = root.typeLabel == "Initializer"

    private fun collectEnclosingClass(): EnclosingElement<GumTreeNode>? {
        val enclosingClassNode = getEnclosingClassNode(root.parent) ?: return null
        val enclosingClassName = enclosingClassNode.getChildOfType(TypeLabels.simpleName)?.originalToken
        return EnclosingElement(
            root = enclosingClassNode,
            type = EnclosingElementType.Class,
            name = enclosingClassName
        )
    }

    private fun getEnclosingClassNode(node: GumTreeNode?): GumTreeNode? {
        if (node == null || node.typeLabel == TypeLabels.typeDeclaration) {
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
            const val singleVariableDeclaration = "SingleVariableDeclaration"
        }
    }
}
