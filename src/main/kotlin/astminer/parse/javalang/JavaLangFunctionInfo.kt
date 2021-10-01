package astminer.parse.javalang

import astminer.common.model.EnclosingElement
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.common.model.SimpleNode

class JavaLangFunctionInfo(override val root: SimpleNode, override val filePath: String) : FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(NAME)

    override val body: SimpleNode?
        get() = super.body

    override val annotations: List<String>?
        get() = super.annotations
    override val modifiers: List<String>?
        get() = super.modifiers
    override val parameters: List<FunctionInfoParameter>?
        get() = super.parameters
    override val returnType: String?
        get() = super.returnType
    override val enclosingElement: EnclosingElement<SimpleNode>?
        get() = super.enclosingElement
    override val isConstructor: Boolean
        get() = super.isConstructor

    companion object {
        const val NAME = "name"
    }
}
