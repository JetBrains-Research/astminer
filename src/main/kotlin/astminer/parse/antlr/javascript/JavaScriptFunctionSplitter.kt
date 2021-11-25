package astminer.parse.antlr.javascript

import astminer.common.model.*
import astminer.parse.antlr.AntlrNode

/**
 * Get all methods (in JavaScript there are divided into functions, arrow functions and methods) and information
 * about their names, enclosing elements and parameters.
 */
class JavaScriptFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    override fun splitIntoFunctions(root: AntlrNode, filePath: String): Collection<FunctionInfo<AntlrNode>> {
        return root.preOrder().mapNotNull { node ->
            when {
                node.isArrowElement() -> JavaScriptArrowInfo(node, filePath)
                node.isFunctionElement() -> JavaScriptFunctionInfo(node, filePath)
                node.isMethodElement() -> JavaScriptMethodInfo(node, filePath)
                else -> null
            }
        }
    }

    private fun AntlrNode.isArrowElement() = this.getChildOfType(ARROW_NODE) != null
    private fun AntlrNode.isFunctionElement() = this.getChildOfType(FUNCTION_NODE) != null
    private fun AntlrNode.isMethodElement() = typeLabel == METHOD_NODE

    companion object {
        private const val METHOD_NODE = "methodDefinition"
        private const val ARROW_NODE = "ARROW"
        private const val FUNCTION_NODE = "Function"
    }
}
