package astminer.parse.gumtree.python

import astminer.common.model.*
import astminer.parse.gumtree.GumTreeNode

class GumTreePythonFunctionSplitter : TreeFunctionSplitter<GumTreeNode> {
    override fun splitIntoFunctions(root: GumTreeNode, filePath: String): Collection<FunctionInfo<GumTreeNode>> {
        val functionRoots = root.preOrder().filter { it.typeLabel == FUNCTION_DECLARATION }
        return functionRoots.map { GumTreePythonFunctionInfo(it, filePath) }
    }

    companion object {
        const val FUNCTION_DECLARATION = "funcdef"
    }
}
