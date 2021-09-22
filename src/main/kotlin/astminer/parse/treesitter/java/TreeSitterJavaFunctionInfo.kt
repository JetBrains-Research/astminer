package astminer.parse.treesitter.java

import astminer.common.model.FunctionInfo
import astminer.common.model.SimpleNode

class TreeSitterJavaFunctionInfo(override val root: SimpleNode, override val filePath: String) :
    FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(FUNCTION_NAME)

    companion object {
        const val FUNCTION_NAME = "identifier"
    }
}
