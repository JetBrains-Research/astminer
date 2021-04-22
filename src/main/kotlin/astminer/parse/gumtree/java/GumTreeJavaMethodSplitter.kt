package astminer.parse.gumtree.java

import astminer.common.model.*
import astminer.common.preOrder
import astminer.parse.gumtree.GumTreeNode

private fun GumTreeNode.isTypeNode() = typeLabel.endsWith("Type")

class GumTreeJavaMethodSplitter : TreeMethodSplitter<GumTreeNode> {

    companion object {
        private object TypeLabels {
            const val methodDeclaration = "MethodDeclaration"
            const val simpleName = "SimpleName"
            const val typeDeclaration = "TypeDeclaration"
            const val singleVariableDeclaration = "SingleVariableDeclaration"
        }
    }

    override fun splitIntoMethods(root: GumTreeNode): Collection<MethodInfo<GumTreeNode>> {
        val methodRoots = root.preOrder().filter { it.typeLabel == TypeLabels.methodDeclaration }
        return methodRoots.map { collectMethodInfo(it as GumTreeNode) }
    }

    private fun collectMethodInfo(methodNode: GumTreeNode): MethodInfo<GumTreeNode> {
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

    private fun getElementName(node: GumTreeNode) = node.children.map {
        it
    }.firstOrNull {
        it.typeLabel == TypeLabels.simpleName
    }

    private fun getElementType(node: GumTreeNode) = node.children.map {
        it
    }.firstOrNull {
        it.isTypeNode()
    }

    private fun getEnclosingClass(node: GumTreeNode): GumTreeNode? {
        if (node.typeLabel == TypeLabels.typeDeclaration) {
            return node
        }
        val parentNode = node.parent
        return parentNode?.let { getEnclosingClass(it) }
    }

    private fun getParameters(methodNode: GumTreeNode): List<ParameterNode<GumTreeNode>> {
        val params = methodNode.children.filter {
            it.typeLabel == TypeLabels.singleVariableDeclaration
        }
        return params.map { node ->
            ParameterNode(
                node,
                getElementType(node),
                getElementName(node)
            )
        }.toList()
    }
}