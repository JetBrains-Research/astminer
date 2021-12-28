package astminer.parse.antlr.python

import astminer.common.model.*
import astminer.parse.antlr.AntlrNode

class PythonFunctionSplitter : TreeFunctionSplitter<AntlrNode> {
    private val methodNode = "funcdef"

    override fun splitIntoFunctions(root: AntlrNode, filePath: String): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            it.typeLabel == methodNode
        }
        return methodRoots.map { AntlrPythonFunctionInfo(it, filePath) }
    }
}
