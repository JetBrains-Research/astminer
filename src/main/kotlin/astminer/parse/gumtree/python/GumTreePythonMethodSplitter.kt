package astminer.parse.gumtree.python

import astminer.common.model.ElementNode
import astminer.common.model.MethodInfo
import astminer.common.model.MethodNode
import astminer.common.model.ParameterNode
import astminer.common.model.TreeMethodSplitter
import astminer.common.preOrder
import astminer.parse.gumtree.GumTreeNode

class GumTreePythonMethodSplitter : TreeMethodSplitter<GumTreeNode> {
    companion object {
        private object TypeLabels {
            const val classDefinition = "ClassDef"
            const val functionDefinition = "FunctionDef"
            const val asyncFunctionDefinition = "AsyncFunctionDef"
            const val nameLoad = "Name_Load"
            const val posOnlyArgs = "posonlyargs"
            const val kwOnlyArgs = "kwonlyargs"
            const val arguments = "arguments"
            const val vararg = "vararg"
            const val kwarg = "kwarg"
            const val args = "args"
            const val arg = "arg"

            const val body = "body"
            const val returnTypeLabel = "Return"
            const val passTypeLabel = "Pass"
            const val constantType = "Constant-"

            val methodDefinitions = listOf(functionDefinition, asyncFunctionDefinition)
            val funcArgsTypesNodes = listOf(args, posOnlyArgs, kwOnlyArgs)
        }
    }

    override fun splitIntoMethods(root: GumTreeNode): Collection<MethodInfo<GumTreeNode>> {
        val methodRoots = root.preOrder().filter { TypeLabels.methodDefinitions.contains(it.typeLabel) }
        return methodRoots.map { collectMethodInfo(it as GumTreeNode) }
    }

    private fun collectMethodInfo(methodNode: GumTreeNode): MethodInfo<GumTreeNode> {
        val methodReturnType = getElementType(methodNode) // no methods return types for current parser
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

    private fun getElementName(node: GumTreeNode) = node

    private fun getElementType(node: GumTreeNode): GumTreeNode? {
        if (node.typeLabel == TypeLabels.arg) {
            return node.getChildOfType(TypeLabels.nameLoad)
        }
        // if return statement has "Constant-`Type`" return value => function type is `Type`
        if (TypeLabels.methodDefinitions.contains(node.typeLabel)) {
            return node.getChildOfType(TypeLabels.body)?.getChildOfType(TypeLabels.returnTypeLabel)?.let {
                it.children.firstOrNull { child ->
                    child.typeLabel.startsWith(TypeLabels.constantType)
                }
            }
        }
        return null
    }

    private fun getEnclosingClass(node: GumTreeNode): GumTreeNode? {
        if (node.typeLabel == TypeLabels.classDefinition) {
            return node
        }
        val parentNode = node.parent
        return parentNode?.let { getEnclosingClass(it) }
    }

    private fun getParameters(methodNode: GumTreeNode): List<ParameterNode<GumTreeNode>> {
        val params = methodNode.getChildrenOfType(TypeLabels.arguments).flatMap {
            it.children
        }.filter {
            TypeLabels.funcArgsTypesNodes.contains(it.typeLabel)
        }.flatMap {
            it.children
        }.filter {
            it.typeLabel == TypeLabels.arg
        } as MutableList

        methodNode.getChildrenOfType(TypeLabels.arguments).flatMap {
            it.children
        }.filter {
            it.typeLabel == TypeLabels.vararg || it.typeLabel == TypeLabels.kwarg
        }.forEach {
            params.add(it)
        }

        return params.map {node ->
            ParameterNode(
                node,
                getElementType(node),
                getElementName(node)
            )
        }.toList()
    }
}
