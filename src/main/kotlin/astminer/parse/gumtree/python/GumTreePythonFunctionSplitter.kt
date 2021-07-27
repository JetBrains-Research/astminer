package astminer.parse.gumtree.python

import astminer.common.model.*
import astminer.parse.gumtree.GumTreeNode

class GumTreePythonFunctionSplitter : TreeFunctionSplitter<GumTreeNode> {
    override fun splitIntoFunctions(root: GumTreeNode, filePath: String): Collection<FunctionInfo<GumTreeNode>> {
        val functionRoots = root.preOrder().filter { TypeLabels.methodDefinitions.contains(it.typeLabel) }
        return functionRoots.map { GumTreePythonFunctionInfo(it, filePath) }
    }

    companion object {
        private object TypeLabels {
            const val functionDefinition = "FunctionDef"
            const val asyncFunctionDefinition = "AsyncFunctionDef"
            val methodDefinitions = listOf(functionDefinition, asyncFunctionDefinition)
        }
    }
}
