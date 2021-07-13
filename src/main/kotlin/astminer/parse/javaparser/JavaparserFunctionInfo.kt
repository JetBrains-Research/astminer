package astminer.parse.javaparser

import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter

class JavaparserFunctionInfo(override val root: JavaParserNode, override val filePath: String) : FunctionInfo<JavaParserNode> {
    companion object {
        const val METHOD_NAME = "SimpleName"
        const val PARAMETER = "Prm"
    }

    override val nameNode: JavaParserNode? = root.getChildOfType(METHOD_NAME)

    override val parameters: List<FunctionInfoParameter>
        get() = super.parameters
    override val returnType: String?
        get() = super.returnType

}