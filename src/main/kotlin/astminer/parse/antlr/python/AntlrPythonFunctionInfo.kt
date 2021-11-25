package astminer.parse.antlr.python

import astminer.common.model.*
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("Antlr-python-function-info")

class AntlrPythonFunctionInfo(override val root: AntlrNode, override val filePath: String) : FunctionInfo<AntlrNode> {
    override val nameNode: AntlrNode? = collectNameNode()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val parameters: List<FunctionInfoParameter>? = extractWithLogger(logger) {
        collectParameters()
    }
    override val isConstructor: Boolean = name == CONSTRUCTOR_FUNCTION_NAME

    private fun collectNameNode(): AntlrNode? = root.getChildOfType(FUNCTION_NAME_NODE)

    private fun collectParameters(): List<FunctionInfoParameter> {
        val parametersRoot = root.getChildOfType(METHOD_PARAMETER_NODE)
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE) ?: return emptyList()

        val methodHaveOnlyOneParameter =
            innerParametersRoot.lastLabelIn(listOf(METHOD_SINGLE_PARAMETER_NODE, PARAMETER_NAME_NODE))
        if (methodHaveOnlyOneParameter) {
            return listOf(assembleMethodInfoParameter(innerParametersRoot))
        }

        return innerParametersRoot.getChildrenOfType(METHOD_SINGLE_PARAMETER_NODE).map { node ->
            assembleMethodInfoParameter(node)
        }
    }

    private fun assembleMethodInfoParameter(parameterNode: AntlrNode): FunctionInfoParameter {
        val parameterHaveNoDefaultOrType = parameterNode.hasLastLabelInBamboo(PARAMETER_NAME_NODE)
        val parameterNameNode = if (parameterHaveNoDefaultOrType) { parameterNode } else {
            parameterNode.traverseDown().getChildOfType(
                PARAMETER_NAME_NODE
            )
        }
        val parameterName = parameterNameNode?.traverseDown()?.token?.original ?: error("Parameter name wasn't found")

        val parameterType = parameterNode.traverseDown().getChildOfType(PARAMETER_TYPE_NODE)?.getTokensFromSubtree()

        return FunctionInfoParameter(
            name = parameterName,
            type = parameterType
        )
    }

    // TODO: refactor remove nested whens
    private fun collectEnclosingElement(): EnclosingElement<AntlrNode>? {
        val enclosingNode = root.findEnclosingElementBy { it.typeLabel in POSSIBLE_ENCLOSING_ELEMENTS } ?: return null
        val type = when (enclosingNode.typeLabel) {
            CLASS_DECLARATION_NODE -> EnclosingElementType.Class
            FUNCTION_NODE ->
                if (enclosingNode.isMethod()) EnclosingElementType.Method else EnclosingElementType.Function
            else -> error("Enclosing node can only be function or class")
        }
        val name = when (type) {
            EnclosingElementType.Class -> enclosingNode.getChildOfType(CLASS_NAME_NODE)
            EnclosingElementType.Method, EnclosingElementType.Function ->
                enclosingNode.traverseDown().getChildOfType(FUNCTION_NAME_NODE)
            else -> error("Enclosing node can only be function or class")
        }?.token?.original
        return EnclosingElement(
            type = type,
            name = name,
            root = enclosingNode
        )
    }

    private fun Node.isMethod(): Boolean {
        val outerBody = traverseUp().parent
        if (outerBody?.typeLabel != BODY) return false

        val enclosingNode = outerBody.parent
        require(enclosingNode != null) { "Found body without enclosing element" }

        val lastLabel = decompressTypeLabel(enclosingNode.typeLabel).last()
        return lastLabel == CLASS_DECLARATION_NODE
    }

    companion object {
        private const val FUNCTION_NODE = "funcdef"
        private const val FUNCTION_NAME_NODE = "NAME"

        private const val CLASS_DECLARATION_NODE = "classdef"
        private const val CLASS_NAME_NODE = "NAME"
        private const val CONSTRUCTOR_FUNCTION_NAME = "__init__"

        private const val METHOD_PARAMETER_NODE = "parameters"
        private const val METHOD_PARAMETER_INNER_NODE = "typedargslist"
        private const val METHOD_SINGLE_PARAMETER_NODE = "tfpdef"
        private const val PARAMETER_NAME_NODE = "NAME"
        private const val PARAMETER_TYPE_NODE = "test"
        // It's seems strange but it works because actual type label will be
        // test|or_test|and_test|not_test|comparison|expr|xor_expr...
        // ..|and_expr|shift_expr|arith_expr|term|factor|power|atom_expr|atom|NAME

        private val POSSIBLE_ENCLOSING_ELEMENTS = listOf(CLASS_DECLARATION_NODE, FUNCTION_NODE)
        private const val BODY = "suite"
    }
}
