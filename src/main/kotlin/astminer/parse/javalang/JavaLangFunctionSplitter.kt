package astminer.parse.javalang

import astminer.common.SimpleNode
import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter

class JavaLangFunctionSplitter : TreeFunctionSplitter<SimpleNode> {
    private val methodDeclarationType = "MethodDeclaration"

    override fun splitIntoFunctions(root: SimpleNode, filePath: String): Collection<FunctionInfo<SimpleNode>> {
        return root.preOrder()
            .filter { it.typeLabel == methodDeclarationType }
            .map { JavaLangFunctionInfo(it, filePath) }
    }
}
