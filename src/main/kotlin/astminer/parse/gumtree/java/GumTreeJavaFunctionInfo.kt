package astminer.parse.gumtree.java

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.gumtree.GumTreeNode

class GumTreeJavaFunctionInfo(override val root: GumTreeNode) : FunctionInfo<GumTreeNode> {
    companion object {
        private object TypeLabels {
            const val simpleName = "SimpleName"
            const val typeDeclaration = "TypeDeclaration"
            const val singleVariableDeclaration = "SingleVariableDeclaration"
        }
    }

    override val nameNode: GumTreeNode? = root.getChildOfType(TypeLabels.simpleName)
    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val returnType: String? = root.getElementType()
    override val enclosingElement: EnclosingElement<GumTreeNode>? = collectEnclosingClass()

    private fun collectEnclosingClass(): EnclosingElement<GumTreeNode>? {
        val enclosingClassNode = getEnclosingClassNode(root.getParent() as GumTreeNode?) ?: return null
        val enclosingClassName = enclosingClassNode.getChildOfType(TypeLabels.simpleName)?.getToken()
        return EnclosingElement(
            root = enclosingClassNode,
            type = EnclosingElementType.Class,
            name = enclosingClassName
        )
    }

    private fun getEnclosingClassNode(node: GumTreeNode?): GumTreeNode? {
        if (node == null || node.getTypeLabel() == TypeLabels.typeDeclaration) {
            return node
        }
        return getEnclosingClassNode(node.getParent() as GumTreeNode?)
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        val params = root.getChildrenOfType(TypeLabels.singleVariableDeclaration)
        return params.map { node ->
            FunctionInfoParameter(
                name = node.getElementName(),
                type = node.getElementType()
            )
        }
    }

    private fun GumTreeNode.getElementName(): String {
        return getChildOfType(TypeLabels.simpleName)?.getToken()
            ?: throw IllegalStateException("No name found for element")
    }

    private fun GumTreeNode.getElementType(): String? {
        return getChildren().firstOrNull { it.isTypeNode() }?.getToken()
    }

    private fun GumTreeNode.isTypeNode() = getTypeLabel().endsWith("Type")
}
