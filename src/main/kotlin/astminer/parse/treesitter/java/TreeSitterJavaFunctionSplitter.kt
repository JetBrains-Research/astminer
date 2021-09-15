package astminer.parse.treesitter.java

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter
import astminer.parse.SimpleNode

class TreeSitterJavaFunctionSplitter : TreeFunctionSplitter<SimpleNode> {
    val methodDeclarationType = "method_declaration"
    override fun splitIntoFunctions(root: SimpleNode, filePath: String): Collection<FunctionInfo<SimpleNode>> {
        return root.preOrder()
            .filter { it.typeLabel == "method_declaration" }
            .map { TreeSitterJavaFunctionInfo(it, filePath) }
    }
}
