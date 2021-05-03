package astminer.parse.gumtree.java

import astminer.common.model.*
import astminer.common.preOrder
import astminer.parse.gumtree.GumTreeNode

class GumTreeJavaMethodSplitter : TreeMethodSplitter<GumTreeNode> {

    companion object {
        private object TypeLabels {
            const val methodDeclaration = "MethodDeclaration"
        }
    }

    override fun splitIntoMethods(root: GumTreeNode): Collection<FunctionInfo<GumTreeNode>> {
        val methodRoots = root.preOrder().filter { it.getTypeLabel() == TypeLabels.methodDeclaration }
        return methodRoots.map { GumTreeJavaFunctionInfo(it as GumTreeNode)}
    }
}