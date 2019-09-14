package astminer.parse.antlr.python

import astminer.common.*
import astminer.parse.antlr.SimpleNode


class PythonMethodSplitter : TreeMethodSplitter<SimpleNode> {

    companion object {
        private const val METHOD_NODE = "funcdef"
        private const val METHOD_NAME_NODE = "NAME"

        private const val CLASS_DECLARATION_NODE = "classdef"
        private const val CLASS_NAME_NODE = "NAME"

        private const val METHOD_PARAMETER_NODE = "parameters"
        private const val METHOD_PARAMETER_INNER_NODE = "typedargslist"
        private const val METHOD_TYPED_PARAMETER_NODE = "tfpdef"
        private const val PARAMETER_NAME_NODE = "NAME"
        private const val PARAMETER_SEPARATOR_NODE = "COMMA"
    }

    override fun splitIntoMethods(root: SimpleNode): Collection<MethodInfo<SimpleNode>> {
        val methodRoots = root.preOrder().filter { it.getTypeLabel() == METHOD_NODE }
        return methodRoots.map { collectMethodInfo(it as SimpleNode) }
    }

    private fun collectMethodInfo(methodNode: SimpleNode): MethodInfo<SimpleNode> {
        val methodName = methodNode.getChildOfType(METHOD_NAME_NODE) as? SimpleNode

        val classRoot = getEnclosingClass(methodNode)
        val className = classRoot?.getChildOfType(CLASS_NAME_NODE) as? SimpleNode

        val parametersRoot = methodNode.getChildOfType(METHOD_PARAMETER_NODE) as? SimpleNode
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE) as? SimpleNode

        val parametersList = if (innerParametersRoot != null) {
            getListOfParameters(innerParametersRoot)
        } else if (parametersRoot != null) {
            getListOfParameters(parametersRoot)
        } else {
            emptyList()
        }

        return MethodInfo(
                MethodNode(methodNode, null, methodName),
                ClassNode(classRoot, className),
                parametersList
        )
    }

    private fun getEnclosingClass(node: SimpleNode): SimpleNode? {
        if (node.getTypeLabel() == CLASS_DECLARATION_NODE) {
            return node
        }
        val parentNode = node.getParent() as? SimpleNode
        if (parentNode != null) {
            return getEnclosingClass(parentNode)
        }
        return null
    }

    private fun getListOfParameters(parameterRoot: SimpleNode): List<ParameterNode<SimpleNode>> {
        var expectParameterName: Boolean = true
        val extractedParameters: MutableList<ParameterNode<SimpleNode>> = mutableListOf()
        for (parameter in parameterRoot.getChildren()) {
            when(parameter.getTypeLabel()) {
                PARAMETER_SEPARATOR_NODE -> expectParameterName = true
                METHOD_TYPED_PARAMETER_NODE -> {
                    val parameterNode = parameter.getChildOfType(PARAMETER_NAME_NODE) as? SimpleNode
                    parameterNode?.let {
                        extractedParameters.add(ParameterNode(it, null, it))
                    }
                    expectParameterName = false
                }
                PARAMETER_NAME_NODE -> {
                    if (expectParameterName) {
                        (parameter as SimpleNode).let {
                            extractedParameters.add(ParameterNode(it, null, it))
                        }
                        expectParameterName = false
                    }
                }
            }
        }
        return extractedParameters
    }
}
