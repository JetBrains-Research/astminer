package astminer.parse.antlr.java

import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.firstLabelIn
import astminer.parse.antlr.hasLastLabel

data class AntlrJavaFunctionInfo(override val root: AntlrNode) : FunctionInfo<AntlrNode> {
    override val nameNode: AntlrNode? = collectNameNode()
    override val parameters: List<MethodInfoParameter> = collectParameters()
    override val returnType: String? = collectReturnType()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingClass(root)

    companion object {
        private const val METHOD_RETURN_TYPE_NODE = "typeTypeOrVoid"
        private const val METHOD_NAME_NODE = "IDENTIFIER"

        private const val CLASS_DECLARATION_NODE = "classDeclaration"
        private const val CLASS_NAME_NODE = "IDENTIFIER"

        private const val METHOD_PARAMETER_NODE = "formalParameters"
        private const val METHOD_PARAMETER_INNER_NODE = "formalParameterList"
        private val METHOD_SINGLE_PARAMETER_NODES = listOf("formalParameter", "lastFormalParameter")
        private const val PARAMETER_RETURN_TYPE_NODE = "typeType"
        private const val PARAMETER_NAME_NODE = "variableDeclaratorId"
    }

    private fun collectNameNode(): AntlrNode? {
        return root.getChildOfType(METHOD_NAME_NODE)
    }

    private fun collectReturnType(): String? {
        val returnTypeNode = root.getChildOfType(METHOD_RETURN_TYPE_NODE)
        return returnTypeNode?.let { collectParameterToken(it) }
        //TODO(check postprocessing)
    }

    private fun collectEnclosingClass(node: AntlrNode?): EnclosingElement<AntlrNode>? {
        return when {
            node == null -> null
            node.hasLastLabel(CLASS_DECLARATION_NODE) -> EnclosingElement(
                type = EnclosingElementType.Class,
                name = node.getChildOfType(CLASS_NAME_NODE)?.getToken(),
                root = node
            )
            else -> collectEnclosingClass(node.getParent() as AntlrNode)
        }
    }

    private fun collectParameters(): List<MethodInfoParameter> {
        val parametersRoot = root.getChildOfType(METHOD_PARAMETER_NODE)
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE) ?: return emptyList()

        if (innerParametersRoot.hasLastLabel(METHOD_SINGLE_PARAMETER_NODES)) {
            return listOf(getParameterInfo(innerParametersRoot))
        }

        return innerParametersRoot.getChildren().filter {
            it.firstLabelIn(METHOD_SINGLE_PARAMETER_NODES)
        }.map { getParameterInfo(it) }
    }

    private fun getParameterInfo(parameterNode: AntlrNode): MethodInfoParameter {
        val returnTypeNode = parameterNode.getChildOfType(PARAMETER_RETURN_TYPE_NODE)
        val returnTypeToken = returnTypeNode?.let { collectParameterToken(it) }

        val parameterName = parameterNode.getChildOfType(PARAMETER_NAME_NODE)?.getToken()
            ?: throw IllegalStateException("Parameter name wasn't found")

        return MethodInfoParameter(parameterName, returnTypeToken)

    }

    //TODO(rename)
    private fun collectParameterToken(parameterNode: AntlrNode): String {
        if (parameterNode.isLeaf()) {
            return parameterNode.getToken()
        }
        return parameterNode.getChildren().joinToString(separator = "") { child ->
            collectParameterToken(child)
        }
    }
}

