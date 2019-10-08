package astminer.parse.antlr.javascript

import astminer.common.model.*
import astminer.common.preOrder
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.decompressTypeLabel

class JavaScriptMethodSplitter : TreeMethodSplitter<SimpleNode> {
    companion object {
        private const val METHOD_NODE = "methodDefinition"
        private const val ARROW_NODE = "ARROW"
        private const val FUNCTION_NODE = "Function"
    }

    override fun splitIntoMethods(root: SimpleNode): Collection<MethodInfo<SimpleNode>> {
        val methodRoots: List<JavaScriptElement> = root.preOrder().map { node ->
            when {
                node.isArrowElement() -> ArrowElement(node as SimpleNode)
                node.isFunctionElement() -> FunctionElement(node as SimpleNode)
                node.isMethodElement() -> MethodElement(node as SimpleNode)
                else -> null
            }
        }.filterNotNull()

        return methodRoots.map { it.getElementInfo() }
    }

    private fun Node.isArrowElement() = this.getChildOfType(ARROW_NODE) != null
    private fun Node.isFunctionElement() = this.getChildOfType(FUNCTION_NODE) != null
    private fun Node.isMethodElement() = decompressTypeLabel(this.getTypeLabel()).last() == METHOD_NODE
}

abstract class JavaScriptElement(private val element: SimpleNode) {
    companion object {
        private const val ENCLOSING_ELEMENT_NODE = "statement"
        private val ENCLOSING_ELEMENT_NODES = listOf("functionDeclaration", "variableDeclaration", "classDeclaration", "methodDefinition", "statement")
        private const val ENCLOSING_ELEMENT_NAME_NODE = "Identifier"

        private const val SINGLE_PARAMETER_NODE = "formalParameterArg"
        private const val PARAMETER_NAME_NODE = "Identifier"
    }

    fun getElementInfo() : MethodInfo<SimpleNode> {
        val enclosingRoot = getEnclosingElement(element.getParent() as SimpleNode)
        return MethodInfo(
                MethodNode(element, null, getElementName()),
                ElementNode(enclosingRoot, getEnclosingElementName(enclosingRoot)),
                getElementParametersList(getElementParametersRoot())
        )
    }

    open fun getEnclosingElement(node: SimpleNode?): SimpleNode? {
        if (node == null || decompressTypeLabel(node.getTypeLabel()).intersect(ENCLOSING_ELEMENT_NODES).isNotEmpty()) {
            return node
        }
        return getEnclosingElement(node.getParent() as? SimpleNode)
    }

    open fun getEnclosingElementName(enclosingRoot: SimpleNode?) : SimpleNode? {
        return enclosingRoot?.getChildOfType(ENCLOSING_ELEMENT_NAME_NODE) as? SimpleNode
    }

    open fun getElementParametersList(parameterRoot: SimpleNode?): List<ParameterNode<SimpleNode>> {
        return when {
            parameterRoot == null -> emptyList()
            parameterRoot.hasLastLabel(PARAMETER_NAME_NODE) -> listOf(ParameterNode(parameterRoot, null, parameterRoot))
            else -> parameterRoot.getItOrChildrenOfType(SINGLE_PARAMETER_NODE).map {
                ParameterNode(it, null, it.getItOrChildrenOfType(PARAMETER_NAME_NODE).firstOrNull())
            }
        }
    }

    private fun Node.hasLastLabel(typeLabel: String): Boolean {
        return decompressTypeLabel(getTypeLabel()).last() == typeLabel
    }

    private fun SimpleNode.getItOrChildrenOfType(typeLabel: String) : List<SimpleNode> {
        return if (hasLastLabel(typeLabel)) {
            listOf(this)
        } else {
            this.getChildrenOfType(typeLabel).mapNotNull { it as? SimpleNode }
        }
    }

    abstract fun getElementName(): SimpleNode?
    abstract fun getElementParametersRoot(): SimpleNode?
}


class ArrowElement(private val element: SimpleNode) : JavaScriptElement(element) {
    companion object {
        private const val ARROW_NAME_NODE = "Identifier"
        private const val ARROW_PARAMETER_NODE = "arrowFunctionParameters"
        private const val ARROW_PARAMETER_INNER_NODE = "formalParameterList"
    }

    override fun getElementName(): SimpleNode? {
        return element.getChildren().firstOrNull {
            it.getTypeLabel() == ARROW_NAME_NODE
        } as? SimpleNode
    }

    override fun getElementParametersRoot(): SimpleNode? {
        val parameterRoot = element.getChildOfType(ARROW_PARAMETER_NODE) as? SimpleNode
        return parameterRoot?.getChildOfType(ARROW_PARAMETER_INNER_NODE) as? SimpleNode ?: parameterRoot
    }
}


class FunctionElement(private val element: SimpleNode) : JavaScriptElement(element) {
    companion object {
        private const val FUNCTION_NAME_NODE = "Identifier"
        private const val FUNCTION_PARAMETER_NODE = "formalParameterList"
    }

    override fun getElementName(): SimpleNode? {
        return element.getChildren().firstOrNull {
            it.getTypeLabel() == FUNCTION_NAME_NODE
        } as? SimpleNode
    }

    override fun getElementParametersRoot(): SimpleNode? {
        return element.getChildOfType(FUNCTION_PARAMETER_NODE) as? SimpleNode
    }
}


class MethodElement(private val element: SimpleNode) : JavaScriptElement(element) {
    companion object {
        private val METHOD_GETTERS_SETTERS = listOf("getter", "setter")
        private const val METHOD_NAME_NODE = "identifierName"
        private const val METHOD_PARAMETER_NODE = "formalParameterList"
    }

    override fun getElementName(): SimpleNode? {
        val methodNameParent = element.getChildren().firstOrNull {
            METHOD_GETTERS_SETTERS.contains(it.getTypeLabel())
        } as? SimpleNode ?: element

        return methodNameParent.getChildren().firstOrNull {
            decompressTypeLabel(it.getTypeLabel()).contains(METHOD_NAME_NODE)
        } as? SimpleNode
    }

    override fun getElementParametersRoot(): SimpleNode? {
        return element.getChildOfType(METHOD_PARAMETER_NODE) as? SimpleNode
    }
}