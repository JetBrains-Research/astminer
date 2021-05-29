package astminer.parse.antlr.php

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter
import astminer.parse.antlr.AntlrNode

class PHPMethodSplitter: TreeFunctionSplitter<AntlrNode> {
    companion object {
        const val LAMBDA_TOKEN = "LambdaFn"
        const val FUNCTION_TOKEN = "Function_"
    }

    override fun splitIntoFunctions(root: AntlrNode): Collection<FunctionInfo<AntlrNode>> {
        return root.preOrder().mapNotNull { node->
            node.parent?.let { statement ->
                when (node.typeLabel) {
                    LAMBDA_TOKEN -> ArrowPhpFunctionInfo(statement)
                    FUNCTION_TOKEN -> SimplePhpFunctionInfo(statement)
                    else -> null
                }
            }
        }
    }
}