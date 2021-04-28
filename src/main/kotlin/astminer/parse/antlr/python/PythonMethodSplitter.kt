package astminer.parse.antlr.python

import astminer.common.*
import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.decompressTypeLabel


class PythonMethodSplitter : TreeMethodSplitter<AntlrNode> {
    private val methodNode = "funcdef"

    override fun splitIntoMethods(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        val methodRoots = root.preOrder().filter {
            decompressTypeLabel(it.getTypeLabel()).last() == methodNode
        }
        return methodRoots.map { AntlrPythonFunctionInfo(it as AntlrNode) }
    }
}
