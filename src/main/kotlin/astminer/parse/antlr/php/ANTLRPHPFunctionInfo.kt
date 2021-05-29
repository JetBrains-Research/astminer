package astminer.parse.antlr.php

import astminer.common.DEFAULT_TOKEN
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.antlr.*

abstract class ANTLRPHPFunctionInfo(
    final override val root: AntlrNode
) : FunctionInfo<AntlrNode> {
    override val parameters: List<FunctionInfoParameter> = collectParameters()

    override val returnType = getElementType(root)

    companion object {
        const val PARAMETERS = "formalParameterList"
        const val ONE_PARAMETER = "formalParameter"
        const val TYPE = "typeHint"
        const val PARAMETER_NAME = "VarName"
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        // No parameters
        val parameterList = root.getChildOfType(PARAMETERS) ?: return emptyList()

        // Checking if function have only one parameter without
        // type or ellipsis
        if (parameterList.hasLastLabel(PARAMETER_NAME)) {
            return listOf(assembleParameter(parameterList))
        }

        // Otherwise find all parameters
        return parameterList.getItOrChildrenOfType(ONE_PARAMETER).map {
            assembleParameter(it)
        }
    }

    private fun assembleParameter(parameterNode: AntlrNode): FunctionInfoParameter {
        return FunctionInfoParameter(
            name = getParameterName(parameterNode),
            type = getElementType(parameterNode)
        )
    }

    private fun getParameterName(parameterNode: AntlrNode): String {
        if (parameterNode.hasLastLabel(PARAMETER_NAME))
            return parameterNode.originalToken ?: return DEFAULT_TOKEN

        return parameterNode.children
            .filter { !it.hasFirstLabel(TYPE) }
            .map { it.originalToken }
            .joinToString("")
    }

    private fun getElementType(element: AntlrNode): String? {
        return element.getChildOfType(TYPE)?.originalToken
    }


}

class ArrowPhpFunctionInfo(root: AntlrNode) : ANTLRPHPFunctionInfo(root) {

}

class SimplePhpFunctionInfo(root: AntlrNode) : ANTLRPHPFunctionInfo(root) {

}