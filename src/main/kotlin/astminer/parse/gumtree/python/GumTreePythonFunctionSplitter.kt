package astminer.parse.gumtree.python

import astminer.common.model.*
import astminer.common.preOrder
import astminer.parse.gumtree.GumTreeNode

class GumTreePythonFunctionSplitter : TreeFunctionSplitter<GumTreeNode> {
    companion object {
        private object TypeLabels {
            const val functionDefinition = "FunctionDef"
            const val asyncFunctionDefinition = "AsyncFunctionDef"
            val methodDefinitions = listOf(functionDefinition, asyncFunctionDefinition)
        }
    }

    override fun splitIntoMethods(root: GumTreeNode): Collection<FunctionInfo<GumTreeNode>> {
        val methodRoots = root.preOrder().filter { TypeLabels.methodDefinitions.contains(it.getTypeLabel()) }
        return methodRoots.map { GumTreeFunctionInfo(it as GumTreeNode) }
    }
}
