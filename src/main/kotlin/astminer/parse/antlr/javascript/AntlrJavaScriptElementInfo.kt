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
            it.containsLabelIn(ENCLOSING_ELEMENT_NODES)
        } ?: return null
        return EnclosingElement(
            type = getEnclosingElementType(enclosingElement),
            name = getEnclosingElementName(enclosingElement),
            root = enclosingElement
        )
    }

    private fun AntlrNode.containsLabelIn(labels: List<String>): Boolean =
        decompressTypeLabel(typeLabel).intersect(labels).isNotEmpty()

    private fun getEnclosingElementName(enclosingRoot: AntlrNode?): String? {
        return enclosingRoot?.children?.firstOrNull {
            it.hasLastLabel(ENCLOSING_ELEMENT_NAME_NODE)
        }?.originalToken
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

    protected fun collectParameters(): List<FunctionInfoParameter> {
        val parametersRoot = getParametersRoot()
        val parameterNameNodes = when {
            // No parameters found
            parametersRoot == null -> emptyList()

            // Have only one parameter, which is indicated only by its name
            parametersRoot.hasLastLabel(PARAMETER_NAME_NODE) -> listOf(parametersRoot)

            // Have many parameters or one indicated not only by it's name
            else ->
                parametersRoot
                    .getItOrChildrenOfType(SINGLE_PARAMETER_NODE)
                    .map { it.getChildOfType(PARAMETER_NAME_NODE) ?: it }
        }
        return parameterNameNodes.map {
            check(it.originalToken != null) { "Parameter name wasn't found" }
            FunctionInfoParameter(name = it.originalToken, type = null)
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
    override val nameNode: AntlrNode? = root.getChildOfType(ARROW_NAME_NODE)

    override val parameters: List<FunctionInfoParameter>? =
        try { collectParameters() } catch (e: IllegalStateException) {
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
        try { collectParameters() } catch (e: IllegalStateException) {
            logger.warn { e.message }
            null
        }

    private fun collectNameNode(): AntlrNode? {
        val methodNameParent = root.children.firstOrNull {
            METHOD_GETTERS_SETTERS.contains(it.typeLabel)
        } ?: root

        return methodNameParent.children.firstOrNull {
            decompressTypeLabel(it.typeLabel).contains(METHOD_NAME_NODE)
        }
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
        try { collectParameters() } catch (e: IllegalStateException) {
            logger.warn { e.message }
            null
        }

    override fun getParametersRoot(): AntlrNode? = root.getChildOfType(FUNCTION_PARAMETER_NODE)

    companion object {
        private const val FUNCTION_NAME_NODE = "Identifier"
        private const val FUNCTION_PARAMETER_NODE = "formalParameterList"
    }
}
