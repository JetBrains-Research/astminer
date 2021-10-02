package astminer.parse.javalang

import astminer.common.model.EnclosingElement
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.common.model.SimpleNode

class JavaLangFunctionInfo(override val root: SimpleNode, override val filePath: String) : FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(NAME)

    override val body: SimpleNode? = root.getChildOfType(BODY)

    override val annotations: List<String>?
        get() = super.annotations
    override val modifiers: List<String>?
        get() = super.modifiers
    override val parameters: List<FunctionInfoParameter>?
        get() = super.parameters

    override val returnType: String = root.children.find { it.typeLabel in possibleTypes }?.extractType() ?: VOID

    override val enclosingElement: EnclosingElement<SimpleNode>?
        get() = super.enclosingElement
    override val isConstructor: Boolean
        get() = super.isConstructor

    private fun SimpleNode.extractType(): String = this.preOrder()
            .mapNotNull { if (it.typeLabel == "dimensions" && it.isLeaf()) "[]" else it.originalToken }
            .joinToString(separator = "")

    companion object {
        const val NAME = "name"
        const val BODY = "body"

        const val VOID = "void"
        val possibleTypes = listOf(
            "BasicType",
            "ReferenceType"
        )
    }
}
