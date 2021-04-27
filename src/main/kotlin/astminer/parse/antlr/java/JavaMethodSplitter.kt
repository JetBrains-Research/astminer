package astminer.parse.antlr.java

import astminer.common.*
import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.decompressTypeLabel

class JavaMethodSplitter : TreeMethodSplitter<AntlrNode> {
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

    override fun splitIntoMethods(root: AntlrNode): Collection<MethodInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            decompressTypeLabel(it.typeLabel).last() == METHOD_NODE
        }
        return methodRoots.map { collectMethodInfo(it) }
    }

    private fun collectMethodInfo(methodNode: AntlrNode): MethodInfo<AntlrNode> {
        val methodName = methodNode.getChildOfType(METHOD_NAME_NODE)
        val methodReturnTypeNode =  methodNode.getChildOfType(METHOD_RETURN_TYPE_NODE)
        methodReturnTypeNode?.let { it.token = collectParameterToken(it) }

        val classRoot = getEnclosingClass(methodNode)
        val className = classRoot?.getChildOfType(CLASS_NAME_NODE)

        val parametersRoot = methodNode.getChildOfType(METHOD_PARAMETER_NODE)
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE)

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

    private fun getEnclosingClass(node: AntlrNode): AntlrNode? {
        if (decompressTypeLabel(node.typeLabel).last() == CLASS_DECLARATION_NODE) {
            return node
        }
        val parentNode = node.parent
        if (parentNode != null) {
            return getEnclosingClass(parentNode)
        }
        return null
    }

    private fun getListOfParameters(parametersRoot: AntlrNode): List<ParameterNode<AntlrNode>> {
        if (METHOD_SINGLE_PARAMETER_NODE.contains(decompressTypeLabel(parametersRoot.typeLabel).last())) {
            return listOf(getParameterInfoFromNode(parametersRoot))
        }
        return parametersRoot.children.filter {
            val firstType = decompressTypeLabel(it.typeLabel).first()
            METHOD_SINGLE_PARAMETER_NODE.contains(firstType)
        }.map {
            getParameterInfoFromNode(it)
        }
    }

    private fun getParameterInfoFromNode(parameterRoot: AntlrNode): ParameterNode<AntlrNode> {
        val returnTypeNode = parameterRoot.getChildOfType(PARAMETER_RETURN_TYPE_NODE)
        returnTypeNode?.let { it.token = collectParameterToken(it) }
        return ParameterNode(
                parameterRoot,
                returnTypeNode,
                parameterRoot.getChildOfType(PARAMETER_NAME_NODE)
        )
    }

    private fun collectParameterToken(parameterRoot: AntlrNode): String {
        if (parameterRoot.isLeaf()) {
            return parameterRoot.token
        }
        return parameterRoot.children.joinToString(separator = "") { child ->
            collectParameterToken(child)
        }
    }
}