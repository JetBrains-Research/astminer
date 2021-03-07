package astminer.parse.python

import astminer.common.model.ElementNode
import astminer.common.model.MethodInfo
import astminer.common.model.MethodNode
import astminer.common.model.ParameterNode
import astminer.common.model.TreeMethodSplitter
import astminer.common.preOrder

class GumTreePythonMethodSplitter : TreeMethodSplitter<GumTreePythonNode> {
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

    override fun splitIntoMethods(root: GumTreePythonNode): Collection<MethodInfo<GumTreePythonNode>> {
        val methodRoots = root.preOrder().filter { TypeLabels.methodDefinitions.contains(it.getTypeLabel()) }
        return methodRoots.map { collectMethodInfo(it as GumTreePythonNode) }
    }

    private fun collectMethodInfo(methodNode: GumTreePythonNode): MethodInfo<GumTreePythonNode> {
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

    private fun getElementName(node: GumTreePythonNode) = node

    private fun getElementType(node: GumTreePythonNode): GumTreePythonNode? {
        if (node.getTypeLabel() == TypeLabels.arg) {
            return node.getChildOfType(TypeLabels.nameLoad) as GumTreePythonNode?
        }
        // if return statement has "Constant-`Type`" return value => function type is `Type`
        if (TypeLabels.methodDefinitions.contains(node.getTypeLabel())) {
            return node.getChildOfType(TypeLabels.body)?.getChildOfType(TypeLabels.returnTypeLabel)?.let {
                it.getChildren().firstOrNull { child ->
                    child.getTypeLabel().startsWith(TypeLabels.constantType)
                } as GumTreePythonNode?
            }
        }
        return null
    }

    private fun getEnclosingClass(node: GumTreePythonNode): GumTreePythonNode? {
        if (node.getTypeLabel() == TypeLabels.classDefinition) {
            return node
        }
        val parentNode = node.getParent() as? GumTreePythonNode
        return parentNode?.let { getEnclosingClass(it) }
    }

    private fun getParameters(methodNode: GumTreePythonNode): List<ParameterNode<GumTreePythonNode>> {
        val params = methodNode.getChildrenOfType(TypeLabels.arguments).flatMap {
            it.getChildren()
        }.filter {
            TypeLabels.funcArgsTypesNodes.contains(it.getTypeLabel())
        }.flatMap {
            it.getChildren()
        }.filter {
            it.getTypeLabel() == TypeLabels.arg
        } as MutableList

        methodNode.getChildrenOfType(TypeLabels.arguments).flatMap {
            it.getChildren()
        }.filter {
            it.getTypeLabel() == TypeLabels.vararg || it.getTypeLabel() == TypeLabels.kwarg
        }.forEach {
            params.add(it)
        }

        return params.map {
            val node = it as GumTreePythonNode
            ParameterNode<GumTreePythonNode>(
                node,
                getElementType(node),
                getElementName(node)
            )
        }.toList()
    }
}
