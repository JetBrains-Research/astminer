package astminer.parse.antlr.java

import astminer.common.*
import astminer.common.model.*
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.decompressTypeLabel

class JavaMethodSplitter : TreeMethodSplitter<SimpleNode> {
    companion object {
        private const val METHOD_NODE = "methodDeclaration"
        private const val METHOD_RETURN_TYPE_NODE = "typeTypeOrVoid"
        private const val METHOD_NAME_NODE = "IDENTIFIER"

        private const val CLASS_DECLARATION_NODE = "classDeclaration"
        private const val CLASS_NAME_NODE = "IDENTIFIER"

        private const val METHOD_PARAMETER_NODE = "formalParameters"
        private const val METHOD_PARAMETER_INNER_NODE = "formalParameterList"
        private val METHOD_SINGLE_PARAMETER_NODE = listOf("formalParameter", "lastFormalParameter")
        private const val PARAMETER_RETURN_TYPE_NODE = "typeType"
        private const val PARAMETER_NAME_NODE = "variableDeclaratorId"
    }

    override fun splitIntoMethods(root: SimpleNode): Collection<MethodInfo<SimpleNode>> {
        val methodRoots = root.preOrder().filter {
            decompressTypeLabel(it.getTypeLabel()).last() == METHOD_NODE
        }
        return methodRoots.map { collectMethodInfo(it as SimpleNode) }
    }

    private fun collectMethodInfo(methodNode: SimpleNode): MethodInfo<SimpleNode> {
        val methodName = methodNode.getChildOfType(METHOD_NAME_NODE) as? SimpleNode
        val methodReturnTypeNode =  methodNode.getChildOfType(METHOD_RETURN_TYPE_NODE) as? SimpleNode
        methodReturnTypeNode?.setToken(collectParameterToken(methodReturnTypeNode))

        val classRoot = getEnclosingClass(methodNode)
        val className = classRoot?.getChildOfType(CLASS_NAME_NODE) as? SimpleNode

        val parametersRoot = methodNode.getChildOfType(METHOD_PARAMETER_NODE) as? SimpleNode
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE) as? SimpleNode

        val parametersList = when {
            innerParametersRoot != null -> getListOfParameters(innerParametersRoot)
            parametersRoot != null -> getListOfParameters(parametersRoot)
            else -> emptyList()
        }

        return MethodInfo(
                MethodNode(methodNode, methodReturnTypeNode, methodName),
                ElementNode(classRoot, className),
                parametersList
        )
    }

    private fun getEnclosingClass(node: SimpleNode): SimpleNode? {
        if (decompressTypeLabel(node.getTypeLabel()).last() == CLASS_DECLARATION_NODE) {
            return node
        }
        val parentNode = node.getParent() as? SimpleNode
        if (parentNode != null) {
            return getEnclosingClass(parentNode)
        }
        return null
    }

    private fun getListOfParameters(parametersRoot: SimpleNode): List<ParameterNode<SimpleNode>> {
        if (METHOD_SINGLE_PARAMETER_NODE.contains(decompressTypeLabel(parametersRoot.getTypeLabel()).last())) {
            return listOf(getParameterInfoFromNode(parametersRoot))
        }
        return parametersRoot.getChildren().filter {
            val firstType = decompressTypeLabel(it.getTypeLabel()).first()
            METHOD_SINGLE_PARAMETER_NODE.contains(firstType)
        }.map {
            getParameterInfoFromNode(it as SimpleNode)
        }
    }

    private fun getParameterInfoFromNode(parameterRoot: SimpleNode): ParameterNode<SimpleNode> {
        val returnTypeNode = parameterRoot.getChildOfType(PARAMETER_RETURN_TYPE_NODE) as? SimpleNode
        returnTypeNode?.setToken(collectParameterToken(returnTypeNode))
        return ParameterNode(
                parameterRoot,
                returnTypeNode,
                parameterRoot.getChildOfType(PARAMETER_NAME_NODE) as? SimpleNode
        )
    }

    private fun collectParameterToken(parameterRoot: SimpleNode): String {
        if (parameterRoot.isLeaf()) {
            return parameterRoot.getToken()
        }
        return parameterRoot.getChildren().joinToString(separator = "") { child ->
            collectParameterToken(child as SimpleNode)
        }
    }
}