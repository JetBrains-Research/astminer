package astminer.parse.javalang

import astminer.common.model.FunctionInfo
import astminer.parse.SimpleNode

class JavaLangFunctionInfo(override val root: SimpleNode, override val filePath: String) : FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(NAME)

    companion object {
        const val NAME = "name"
    }
}
