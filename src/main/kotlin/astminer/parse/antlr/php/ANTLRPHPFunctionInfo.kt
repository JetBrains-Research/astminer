package astminer.parse.antlr.php

import astminer.common.model.EnclosingElement
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.antlr.*

class ANTLRPHPFunctionInfo(override val root: AntlrNode) : FunctionInfo<AntlrNode> {
    override val returnType = getElementType(root)
    override val nameNode: AntlrNode? = root.getChildOfType(FUNCTION)

    override val parameters: List<FunctionInfoParameter> = collectParameters()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()

    companion object {
        const val PARAMETERS_LIST = "formalParameterList"
        const val PARAMETER = "formalParameter"
        const val TYPE = "typeHint"
        const val PARAMETER_NAME = "VarName"
        const val CLASS_MEMBER = "classStatement"
        const val FUNCTION = "functionDeclaration"
        const val FUNCTION_NAME = "Identifier"
        const val LAMBDA_DECLARATION = "lambdaFunctionExpr"
        const val CLASS_DECLARATION = "classDeclaration"
        const val VAR_DECLARATION = "variableInitializer"
        const val ELLIPSIS = "Ellipsis"
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        // Parameters in this grammar have following structure:
        //formal parameter list -> formal parameter -> type hint
        //                                        | -> ellipsis
        //                                        | -> var init -> var name
        //                                                    | -> equal
        //                                                    | -> default value

        // No parameters
        val parameterList = root.getChildOfType(PARAMETERS_LIST) ?: return emptyList()

        // Checking if function have only one parameter
        // without ellipsis, type hint or default value
        if (parameterList.hasLastLabel(PARAMETER_NAME) || parameterList.hasLastLabel(VAR_DECLARATION)) {
            return listOf(assembleParameter(parameterList))
        }

        // Otherwise find all parameters
        return parameterList.getItOrChildrenOfType(PARAMETER).mapNotNull {
            try { assembleParameter(it) } catch (e: IllegalStateException) { return@mapNotNull null }
        }
    }

    private fun assembleParameter(parameterNode: AntlrNode): FunctionInfoParameter {
        return FunctionInfoParameter(
            name = getParameterName(parameterNode),
            type = getElementType(parameterNode)
        )
    }

    private fun getParameterName(parameterNode: AntlrNode): String {
        // "..." in php equivalent to *args in python
        val isSplattedArg = parameterNode.getChildOfType(ELLIPSIS) != null

        if (parameterNode.hasLastLabel(PARAMETER_NAME)) return parameterNode.originalToken
            ?: throw IllegalStateException("No name was found for a parameter")

        val varInit = parameterNode.getItOrChildrenOfType(VAR_DECLARATION).first()

        val name = varInit.getItOrChildrenOfType(PARAMETER_NAME).first().originalToken
            ?: throw IllegalStateException("No name was found for a parameter")

        return (if (isSplattedArg) "..." else "") + name
    }

    private fun getElementType(element: AntlrNode): String? {
        return element.getChildOfType(TYPE)?.originalToken
    }

    private fun collectEnclosingElement(): EnclosingElement<AntlrNode>? {
        return null
    }
}