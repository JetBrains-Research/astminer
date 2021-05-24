package astminer.parse.fuzzy.cpp

import astminer.common.*
import astminer.common.model.*

class FuzzyFunctionSplitter : TreeFunctionSplitter<FuzzyNode> {
    private val methodNode = "METHOD"

    override fun splitIntoFunctions(root: FuzzyNode): Collection<FunctionInfo<FuzzyNode>> {
        val methodRoots = root.preOrder().filter { it.typeLabel == methodNode }
        return methodRoots.map { FuzzyCppFunctionInfo(it) }
    }
}