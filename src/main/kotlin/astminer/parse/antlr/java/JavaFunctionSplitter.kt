package astminer.parse.antlr.java

import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.hasLastLabel

class JavaFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    private val methodNodeType = "methodDeclaration"

    override fun collectFunctionInfo(root: AntlrNode, filePath: String): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            (it).hasLastLabel(methodNodeType)
        }
        return methodRoots.map { AntlrJavaFunctionInfo(it, filePath) }
    }
}