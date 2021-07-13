package astminer.parse.javaparser

import astminer.common.model.FunctionInfo
import astminer.common.model.TreeFunctionSplitter

class JavaparserMethodSplitter: TreeFunctionSplitter<JavaParserNode> {
    val METHOD_DECLARATION = "Mth"

    override fun splitIntoFunctions(root: JavaParserNode, filePath: String): Collection<FunctionInfo<JavaParserNode>> {
        return root.preOrder().filter { it.typeLabel == METHOD_DECLARATION }.map { JavaparserFunctionInfo(it, filePath) }
    }
}