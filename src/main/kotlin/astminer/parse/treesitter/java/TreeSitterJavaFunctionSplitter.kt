package astminer.parse.treesitter.java

import astminer.common.SimpleNode
import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter

class TreeSitterJavaFunctionSplitter : TreeFunctionSplitter<SimpleNode> {
    override fun splitIntoFunctions(root: SimpleNode, filePath: String): Collection<FunctionInfo<SimpleNode>> {
        return root.preOrder()
            .filter { it.typeLabel == METHOD_DECLARATION_TYPE }
            .map { TreeSitterJavaFunctionInfo(it, filePath) }
    }
    companion object {
        const val METHOD_DECLARATION_TYPE = "method_declaration"
    }
}
