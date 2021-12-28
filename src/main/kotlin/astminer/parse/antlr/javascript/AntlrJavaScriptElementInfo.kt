package astminer.parse.antlr.javascript

import astminer.common.model.*
import astminer.parse.antlr.*
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("Antlr-Javascript-function-info")

/**
Base class for describing JavaScript methods, functions or arrow functions.
 */
abstract class AntlrJavaScriptElementInfo(override val root: AntlrNode, override val filePath: String) :
    FunctionInfo<AntlrNode> {

    protected fun collectEnclosingElement(): EnclosingElement<AntlrNode>? {
        val enclosingElement = root.findEnclosingElementBy {
            it.typeLabel in ENCLOSING_ELEMENT_NODES
        } ?: return null
        val type = getEnclosingElementType(enclosingElement)
        val name = getEnclosingElementName(enclosingElement)
        return EnclosingElement(type, name, root)
    }

    private fun getEnclosingElementName(enclosingRoot: AntlrNode?): String? {
        return enclosingRoot?.preOrder()?.firstOrNull {
            it.typeLabel == ENCLOSING_ELEMENT_NAME_NODE
        }?.token?.original
    }

    private fun getEnclosingElementType(enclosingRoot: AntlrNode): EnclosingElementType {
        return when (decompressTypeLabel(enclosingRoot.typeLabel).last()) {
            "functionDeclaration" -> EnclosingElementType.Function
            "classDeclaration" -> EnclosingElementType.Class
            "methodDefinition" -> EnclosingElementType.Method
            "variableDeclaration" -> EnclosingElementType.VariableDeclaration
            else -> error("Couldn't derive enclosing element type")
        }
    }

    protected fun collectParameters(): List<FunctionInfoParameter>? = extractWithLogger(logger) {
        // No parameters
        val parametersRoot = getParametersRoot() ?: return@extractWithLogger emptyList()

        // One simple parameter
        if (parametersRoot.children.size == 1 && parametersRoot.children.first().typeLabel == PARAMETER_NAME_NODE) {
            val name = parametersRoot.children.first().token.original ?: error("Parameter with no name")
            return@extractWithLogger listOf(FunctionInfoParameter(name, null))
        }

        // Many parameters or one with default
        val parameterNodes = parametersRoot.children.filter { it.typeLabel == SINGLE_PARAMETER_NODE }
        return@extractWithLogger parameterNodes.map { parameter ->
            val name = parameter.preOrder().find { it.typeLabel == PARAMETER_NAME_NODE }?.token?.original
                ?: error("Parameter with no name")
            FunctionInfoParameter(name, type = null)
        }
    }

    abstract fun getParametersRoot(): AntlrNode?

    companion object {
        private val ENCLOSING_ELEMENT_NODES =
            listOf("functionDeclaration", "variableDeclaration", "classDeclaration", "methodDefinition")
        private const val ENCLOSING_ELEMENT_NAME_NODE = "Identifier"

        private const val SINGLE_PARAMETER_NODE = "formalParameterArg"
        private const val PARAMETER_NAME_NODE = "Identifier"
    }
}

class JavaScriptArrowInfo(root: AntlrNode, filePath: String) : AntlrJavaScriptElementInfo(root, filePath) {

    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val nameNode: AntlrNode? = root.traverseDown().getChildOfType(ARROW_NAME_NODE)

    override val parameters: List<FunctionInfoParameter>? =
        try {
            collectParameters()
        } catch (e: IllegalStateException) {
            logger.warn { e.message }
            null
        }

    override fun getParametersRoot(): AntlrNode? {
        val parameterRoot = root.getChildOfType(ARROW_PARAMETER_NODE)
        return parameterRoot?.getChildOfType(ARROW_PARAMETER_INNER_NODE) ?: parameterRoot
    }

    companion object {
        private const val ARROW_NAME_NODE = "Identifier"
        private const val ARROW_PARAMETER_NODE = "arrowFunctionParameters"
        private const val ARROW_PARAMETER_INNER_NODE = "formalParameterList"
    }
}

class JavaScriptMethodInfo(root: AntlrNode, filePath: String) : AntlrJavaScriptElementInfo(root, filePath) {

    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val nameNode: AntlrNode? = collectNameNode()
    override val parameters: List<FunctionInfoParameter>? =
        try {
            collectParameters()
        } catch (e: IllegalStateException) {
            logger.warn { e.message }
            null
        }

    private fun collectNameNode(): AntlrNode? {
        val methodNameParent = root.children.firstOrNull {
            it.typeLabel in METHOD_GETTERS_SETTERS
        } ?: root

        return methodNameParent.preOrder().firstOrNull {
            it.typeLabel == METHOD_NAME_NODE
        }?.traverseDown()
    }

    override fun getParametersRoot(): AntlrNode? = root.getChildOfType(METHOD_PARAMETER_NODE)

    companion object {
        private val METHOD_GETTERS_SETTERS = listOf("getter", "setter")
        private const val METHOD_NAME_NODE = "identifierName"
        private const val METHOD_PARAMETER_NODE = "formalParameterList"
    }
}

class JavaScriptFunctionInfo(root: AntlrNode, filePath: String) : AntlrJavaScriptElementInfo(root, filePath) {

    override val enclosingElement: EnclosingElement<AntlrNode>? = collectEnclosingElement()
    override val nameNode: AntlrNode? = root.getChildOfType(FUNCTION_NAME_NODE)
    override val parameters: List<FunctionInfoParameter>? =
        try {
            collectParameters()
        } catch (e: IllegalStateException) {
            logger.warn { e.message }
            null
        }

    override fun getParametersRoot(): AntlrNode? = root.getChildOfType(FUNCTION_PARAMETER_NODE)

    companion object {
        private const val FUNCTION_NAME_NODE = "Identifier"
        private const val FUNCTION_PARAMETER_NODE = "formalParameterList"
    }
}
