package astminer.parse.antlr.python

import astminer.common.*
import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.hasLastLabel


class PythonFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    private val methodNode = "funcdef"

    override fun splitIntoMethods(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            (it as AntlrNode).hasLastLabel(methodNode)
        }
        return methodRoots.map { AntlrPythonFunctionInfo(it as AntlrNode) }
    }
}
