package astminer.parse.gumtree.python

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.antlr.getTokensFromSubtree
import astminer.parse.findEnclosingElementBy
import astminer.parse.gumtree.GumTreeNode
import mu.KotlinLogging

private val logger = KotlinLogging.logger("Gumtree-Java-function-info")

class GumTreePythonFunctionInfo(
    override val root: GumTreeNode,
    override val filePath: String
) : FunctionInfo<GumTreeNode> {
    override val nameNode: GumTreeNode? = root.getChildOfType(NAME)

    override val returnType = if (root.children.find { it.originalToken == RETURN_TYPE_OPERATOR } == null) {
        null
    } else root.getChildrenOfType(NAME).lastOrNull()?.preOrder()?.mapNotNull { it.originalToken }?.joinToString("")

    override val enclosingElement: EnclosingElement<GumTreeNode>? = extractWithLogger(logger) {
        val enclosing = root.findEnclosingElementBy { it.typeLabel in possibleEnclosingElements }
            ?: return@extractWithLogger null
        val type = when (enclosing.typeLabel) {
            FUNCTION_DECLARATION -> EnclosingElementType.Function
            CLASS_DECLARATION -> EnclosingElementType.Class
            else -> error("No enclosing type can be associated")
        }
        EnclosingElement(
            name = enclosing.getChildOfType(NAME)?.originalToken,
            type = type,
            root = enclosing
        )
    }
    override val parameters: List<FunctionInfoParameter>? = extractWithLogger(logger) {
        val parameters = root.getChildOfType(PARAMETERS)
        checkNotNull(parameters) { "Node has no parameter node" }
        parameters.children.filter { it.typeLabel == PARAMETER }.map { param ->
            if (param.getChildOfType(TYPE_DEFINITION) == null) {
                FunctionInfoParameter(
                    name = checkNotNull(param.getChildOfType(NAME)?.originalToken),
                    type = null
                )
            } else {
                val variableDef = param.getChildOfType(TYPE_DEFINITION)
                    ?: error("Tree structure was changed while function info collection")
                val name = variableDef.getChildOfType(NAME)?.originalToken
                    ?: error("Parameter has no name")
                val type = if (variableDef.children.size > 1) variableDef.children[1].getTokensFromSubtree() else null
                FunctionInfoParameter(name, type)
            }
        }
    }

    companion object {
        const val NAME = "name"
        const val TYPE = "type"
        const val TYPE_DEFINITION = "tfpdef"
        const val PARAMETERS = "parameters"
        const val PARAMETER = "param"
        const val RETURN_TYPE_OPERATOR = "->"
        const val FUNCTION_DECLARATION = "funcdef"
        const val CLASS_DECLARATION = "classdef"
        val possibleEnclosingElements = listOf(FUNCTION_DECLARATION, CLASS_DECLARATION)
    }

//    override val isConstructor: Boolean = name == TypeLabels.constructorFunctionName
//
//    private fun getElementType(node: GumTreeNode): GumTreeNode? {
//        if (node.typeLabel == TypeLabels.arg) {
//            return node.getChildOfType(TypeLabels.nameLoad)
//        }
//        // if return statement has "Constant-`Type`" return value => function type is `Type`
//        if (TypeLabels.methodDefinitions.contains(node.typeLabel)) {
//            return node.getChildOfType(TypeLabels.body)?.getChildOfType(TypeLabels.returnTypeLabel)?.let {
//                it.children.firstOrNull { child ->
//                    child.typeLabel.startsWith(TypeLabels.constantType)
//                }
//            }
//        }
//        return null
//    }
//
//    companion object {
//        private object TypeLabels {
//            const val NAME = "name"
//
//            const val classDefinition = "ClassDef"
//            const val constructorFunctionName = "__init__"
//            const val functionDefinition = "FunctionDef"
//            const val asyncFunctionDefinition = "AsyncFunctionDef"
//            const val nameLoad = "Name_Load"
//            const val posOnlyArgs = "posonlyargs"
//            const val kwOnlyArgs = "kwonlyargs"
//            const val arguments = "arguments"
//            const val vararg = "vararg"
//            const val kwarg = "kwarg"
//            const val args = "args"
//            const val arg = "arg"
//
//            const val body = "body"
//            const val returnTypeLabel = "Return"
//            const val passTypeLabel = "Pass"
//            const val constantType = "Constant-"
//
//            val methodDefinitions = listOf(functionDefinition, asyncFunctionDefinition)
//            val funcArgsTypesNodes = listOf(args, posOnlyArgs, kwOnlyArgs)
//        }
//    }
}
