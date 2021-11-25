package astminer.parse.antlr.php

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("ANTLR-PHP-function-info")

class ANTLRPHPFunctionInfo(override val root: AntlrNode, override val filePath: String) : FunctionInfo<AntlrNode> {
    override val returnType = getElementType(root)
    override val nameNode: AntlrNode? = root.getChildOfType(FUNCTION_NAME)?.traverseDown()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val parameters: List<FunctionInfoParameter>? = extractWithLogger(logger) {
        val parameters = collectParameters()
        parameters
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        // Parameters in this grammar have following structure (children order may be wrong):
        // formal parameter list -> formal parameter -> Ampersand
        //                                        | -> type hint
        //                                        | -> ellipsis
        //                                        | -> var init -> var name
        //                                                    | -> equal
        //                                                    | -> default value

        // No parameters
        val parameterList = root.getChildOfType(PARAMETERS_LIST) ?: return emptyList()

        // Otherwise find all parameters
        return parameterList.getChildrenOfType(PARAMETER).mapNotNull {
            try {
                assembleParameter(it)
            } catch (e: IllegalStateException) {
                logger.warn { "Error during collecting parameters for $name in $filePath: ${e.message}" }
                null
            }
        }
    }

    private fun assembleParameter(parameterNode: AntlrNode): FunctionInfoParameter {
        val parameter = FunctionInfoParameter(
            name = getParameterName(parameterNode),
            type = getElementType(parameterNode)
        )
        return parameter
    }

    private fun getParameterName(parameterNode: AntlrNode): String {
        // "...$args" in php equivalent to *args in python
        val isSplattedArg = parameterNode.traverseDown().getChildOfType(ELLIPSIS) != null

        val isPassedByReference = parameterNode.getChildOfType(REFERENCE) != null

        if (parameterNode.hasLastLabelInBamboo(PARAMETER_NAME)) {
            return parameterNode.traverseDown().token.original ?: error("No name was found for a parameter")
        }

        val varInit = parameterNode.getChildOfType(VAR_DECLARATION)

        val name = varInit?.traverseDown()?.getItOrChildrenOfType(PARAMETER_NAME)?.firstOrNull()?.token?.original
            ?: error("No name was found for a parameter")

        return (if (isPassedByReference) "&" else "") + (if (isSplattedArg) "..." else "") + name
    }

    private fun getElementType(element: AntlrNode): String? =
        element.getChildOfType(TYPE)?.traverseDown()?.token?.original

    private fun collectEnclosingElement(): EnclosingElement<AntlrNode>? {
        val enclosing = root.findEnclosingElementBy { it.isPossibleEnclosing() } ?: return null
        return try {
            EnclosingElement(
                root = enclosing,
                name = getEnclosingElementName(enclosing),
                type = getEnclosingType(enclosing)
            )
        } catch (e: IllegalStateException) {
            logger.warn { "Error during collecting enclosing element for $name in $filePath: ${e.message}" }
            null
        }
    }

    private fun getEnclosingType(enclosing: AntlrNode): EnclosingElementType {
        return when {
            enclosing.isMethod() -> EnclosingElementType.Method
            enclosing.isFunction() -> EnclosingElementType.Function
            enclosing.isClass() -> EnclosingElementType.Class
            enclosing.isAssignExpression() -> EnclosingElementType.VariableDeclaration
            else -> error("No type can be associated")
        }
    }

    private fun getEnclosingElementName(enclosing: AntlrNode): String? = when {
        enclosing.isFunction() || enclosing.isClass() ->
            enclosing.getChildOfType(FUNCTION_NAME)?.traverseDown()?.token?.original
        enclosing.isAssignExpression() ->
            enclosing.children.find { it.hasLastLabelInBamboo(PARAMETER_NAME) }?.traverseDown()?.token?.original
        else -> error("No type can be associated")
    }

    // No check for method because method is a function
    private fun AntlrNode.isPossibleEnclosing() = isFunction() || isClass() || isAssignExpression()

    private fun AntlrNode.isMethod() = isFunction() && hasFirstLabelInBamboo(CLASS_MEMBER)

    private fun AntlrNode.isFunction() = getChildOfType(LAMBDA_TOKEN) != null || getChildOfType(FUNCTION_TOKEN) != null

    private fun AntlrNode.isAssignExpression() = hasFirstLabelInBamboo(EXPRESSION) && getChildOfType(ASSIGN_OP) != null

    private fun AntlrNode.isClass(): Boolean = hasLastLabelInBamboo(CLASS_DECLARATION)

    companion object {
        const val PARAMETERS_LIST = "formalParameterList"
        const val PARAMETER = "formalParameter"
        const val TYPE = "typeHint"
        const val PARAMETER_NAME = "VarName"
        const val CLASS_MEMBER = "classStatement"
        const val FUNCTION_NAME = "identifier"
        const val CLASS_DECLARATION = "classDeclaration"
        const val VAR_DECLARATION = "variableInitializer"
        const val ELLIPSIS = "Ellipsis"
        const val EXPRESSION = "expression"
        const val ASSIGN_OP = "assignmentOperator"
        const val LAMBDA_TOKEN = "LambdaFn"
        const val FUNCTION_TOKEN = "Function_"
        const val REFERENCE = "Ampersand"
    }
}
