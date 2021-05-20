package astminer.parse.antlr.java

import astminer.common.*
import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.hasLastLabel

class JavaFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    private val methodNodeType = "methodDeclaration"

    override fun splitIntoFunctions(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            (it).hasLastLabel(methodNodeType)
        }
        return methodRoots.map { AntlrJavaFunctionInfo(it) }
    }
}