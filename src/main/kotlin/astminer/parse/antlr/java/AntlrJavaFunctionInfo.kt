package astminer.parse.antlr.java

import astminer.common.model.*
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("Antlr-Java-function-info")

class AntlrJavaFunctionInfo(override val root: AntlrNode, override val filePath: String) : FunctionInfo<AntlrNode> {
    override val nameNode: AntlrNode? = collectNameNode()
    override val returnType: String? = collectReturnType()
    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingClass()

    override val isConstructor: Boolean = false

    override val parameters: List<FunctionInfoParameter>? =
        try {
            collectParameters()
        } catch (e: IllegalStateException) {
            logger.warn { e.message }
            null
        }

    override val modifiers: List<String>? = run {
        root.traverseUp().parent?.children
            ?.filter { it.typeLabel == METHOD_MODIFIER && !it.hasLastLabelInBamboo(METHOD_ANNOTATION) }
            ?.mapNotNull { it.traverseDown().token.original }
    }

    override val annotations: List<String>? = run {
        val declaration = root.traverseUp().parent
        val annotationModifiers = declaration?.children?.filter { it.hasLastLabelInBamboo(METHOD_ANNOTATION) }
        val annotations = annotationModifiers?.map { it.traverseDown().getChildOfType(ANNOTATION_NAME) }
        annotations?.mapNotNull { it?.getChildOfType(IDENTIFIER_NODE)?.token?.original }
    }

    override val body: AntlrNode? = root.children.find { it.typeLabel == METHOD_BODY_NODE }

    override fun isBlank(): Boolean {
        if (body == null) return true
        val block = body.getChildOfType(METHOD_BLOCK_NODE) ?: return true
        return block.children.size <= 2
    }

    private fun collectNameNode(): AntlrNode? = root.getChildOfType(IDENTIFIER_NODE)

    private fun collectReturnType(): String? {
        val returnTypeNode = root.getChildOfType(METHOD_RETURN_TYPE_NODE)
        return returnTypeNode?.getTokensFromSubtree()
    }

    private fun collectEnclosingClass(): EnclosingElement<AntlrNode>? = extractWithLogger(logger) {
        val enclosingClassNode = root
            .findEnclosingElementBy { it.lastLabelIn(possibleEnclosingElements) } ?: return@extractWithLogger null
        val enclosingType = when {
            enclosingClassNode.hasLastLabelInBamboo(CLASS_DECLARATION_NODE) -> EnclosingElementType.Class
            enclosingClassNode.hasLastLabelInBamboo(ENUM_DECLARATION_NODE) -> EnclosingElementType.Enum
            else -> error("No enclosing element type found")
        }
        EnclosingElement(
            type = enclosingType,
            name = enclosingClassNode.getChildOfType(ENCLOSING_NAME_NODE)?.token?.original,
            root = enclosingClassNode
        )
    }

    private fun collectParameters(): List<FunctionInfoParameter> {
        val parametersRoot = root.getChildOfType(METHOD_PARAMETER_NODE)
        val innerParametersRoot = parametersRoot?.getChildOfType(METHOD_PARAMETER_INNER_NODE) ?: return emptyList()

        return innerParametersRoot.children.filter {
            it.typeLabel in METHOD_SINGLE_PARAMETER_NODES
        }.map { singleParameter -> getParameterInfo(singleParameter) }
    }

    private fun getParameterInfo(parameterNode: AntlrNode): FunctionInfoParameter {
        val returnTypeNode = parameterNode.getChildOfType(PARAMETER_RETURN_TYPE_NODE)
        val returnTypeToken = returnTypeNode?.getTokensFromSubtree()

        val parameterName = parameterNode.getChildOfType(PARAMETER_NAME_NODE)?.getTokensFromSubtree()
            ?: error("Parameter name wasn't found")

        return FunctionInfoParameter(parameterName, returnTypeToken)
    }

    companion object {
        private const val METHOD_RETURN_TYPE_NODE = "typeTypeOrVoid"
        private const val METHOD_MODIFIER = "modifier"
        private const val METHOD_ANNOTATION = "annotation"
        private const val ANNOTATION_NAME = "qualifiedName"
        private const val IDENTIFIER_NODE = "IDENTIFIER"
        private const val METHOD_BODY_NODE = "methodBody"
        private const val METHOD_BLOCK_NODE = "block"

        private const val CLASS_DECLARATION_NODE = "classDeclaration"
        private const val ENUM_DECLARATION_NODE = "enumDeclaration"
        val possibleEnclosingElements = listOf(
            CLASS_DECLARATION_NODE,
            ENUM_DECLARATION_NODE
        )
        private const val ENCLOSING_NAME_NODE = "IDENTIFIER"

        private const val METHOD_PARAMETER_NODE = "formalParameters"
        private const val METHOD_PARAMETER_INNER_NODE = "formalParameterList"
        private val METHOD_SINGLE_PARAMETER_NODES = listOf("formalParameter", "lastFormalParameter")
        private const val PARAMETER_RETURN_TYPE_NODE = "typeType"
        private const val PARAMETER_NAME_NODE = "variableDeclaratorId"
    }
}
