package astminer.parse.antlr.java

import astminer.common.*
import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.hasLastLabel

class JavaMethodSplitter : TreeMethodSplitter<AntlrNode> {
    private val methodNodeType = "methodDeclaration"

    override fun splitIntoMethods(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            it.hasLastLabel(methodNodeType)
        }
        return methodRoots.map { AntlrJavaFunctionInfo(it as AntlrNode) }
    }
}