package astminer.parse.antlr.php

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter
import astminer.parse.antlr.AntlrNode

class PHPMethodSplitter: TreeFunctionSplitter<AntlrNode> {
    companion object {
        const val CLASS_MEMBER = "classStatement"
        const val FUNCTION_TOKEN = "Function_"

    }

    override fun splitIntoFunctions(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        TODO("implement")
    }
}