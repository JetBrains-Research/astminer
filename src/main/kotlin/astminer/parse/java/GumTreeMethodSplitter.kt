package astminer.parse.java

import astminer.common.model.*
import astminer.common.preOrder

private fun GumTreeJavaNode.isTypeNode() = getTypeLabel().endsWith("Type")

class GumTreeMethodSplitter : TreeMethodSplitter<GumTreeJavaNode> {

    companion object {
        private object TypeLabels {
            const val methodDeclaration = "MethodDeclaration"
            const val simpleName = "SimpleName"
            const val typeDeclaration = "TypeDeclaration"
            const val singleVariableDeclaration = "SingleVariableDeclaration"
        }
    }

    override fun splitIntoMethods(root: GumTreeJavaNode): Collection<MethodInfo<GumTreeJavaNode>> {
        val methodRoots = root.preOrder().filter { it.getTypeLabel() == TypeLabels.methodDeclaration }
        return methodRoots.map { collectMethodInfo(it as GumTreeJavaNode) }
    }

    private fun collectMethodInfo(methodNode: GumTreeJavaNode): MethodInfo<GumTreeJavaNode> {
        val methodReturnType = getElementType(methodNode)
        val methodName = getElementName(methodNode)

        val classRoot = getEnclosingClass(methodNode)
        val className = classRoot?.let { getElementName(it) }

        val parameters = getParameters(methodNode)

        return MethodInfo(
                MethodNode(methodNode, methodReturnType, methodName),
                ElementNode(classRoot, className),
                parameters
        )
    }

    private fun getElementName(node: GumTreeJavaNode) = node.getChildren().map {
        it as GumTreeJavaNode
    }.firstOrNull {
        it.getTypeLabel() == TypeLabels.simpleName
    }

    private fun getElementType(node: GumTreeJavaNode) = node.getChildren().map {
        it as GumTreeJavaNode
    }.firstOrNull {
        it.isTypeNode()
    }

    private fun getEnclosingClass(node: GumTreeJavaNode): GumTreeJavaNode? {
        if (node.getTypeLabel() == TypeLabels.typeDeclaration) {
            return node
        }
        val parentNode = node.getParent() as? GumTreeJavaNode
        return parentNode?.let { getEnclosingClass(it) }
    }

    private fun getParameters(methodNode: GumTreeJavaNode): List<ParameterNode<GumTreeJavaNode>> {
        val params = methodNode.getChildren().filter {
            it.getTypeLabel() == TypeLabels.singleVariableDeclaration
        }
        return params.map {
            val node = it as GumTreeJavaNode
            ParameterNode<GumTreeJavaNode>(
                    node,
                    getElementType(node),
                    getElementName(node)
            )
        }.toList()
    }
}