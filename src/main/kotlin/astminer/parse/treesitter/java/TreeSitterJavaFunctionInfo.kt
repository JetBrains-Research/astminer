package astminer.parse.treesitter.java

import astminer.common.EMPTY_TOKEN
import astminer.common.model.EnclosingElement
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.common.model.SimpleNode
import astminer.parse.antlr.getTokensFromSubtree

class TreeSitterJavaFunctionInfo(override val root: SimpleNode, override val filePath: String) :
    FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(FUNCTION_NAME)

    override val body: SimpleNode? = root.getChildOfType(BODY)

    override val annotations: List<String>?
        get() = super.annotations

    override val modifiers: List<String>?
        get() = super.modifiers
    override val parameters: List<FunctionInfoParameter>?
        get() = super.parameters
    override val returnType: String = run {
        val returnTypeNode = root.children.find { returnTypes.contains(it.typeLabel) }
        checkNotNull(returnTypeNode) { "No return type in function $name in file $filePath" }

        var collectedType = returnTypeNode.getTokensFromSubtree()
        if (returnTypeNode.typeLabel == ARRAY_TYPE) {
            collectedType = collectedType.replace(EMPTY_TOKEN, "[]")
        }
        return@run collectedType
    }

    override val enclosingElement: EnclosingElement<SimpleNode>?
        get() = super.enclosingElement

    override val isConstructor: Boolean
        get() = super.isConstructor

    companion object {
        const val FUNCTION_NAME = "identifier"
        const val BODY = "block"
        const val ARRAY_TYPE = "array_type"
        val returnTypes = listOf(
            "void_type",
            "integral_type",
            "floating_point_type",
            "boolean_type",
            "type_identifier",
            "scoped_type_identifier",
            "generic_type",
            ARRAY_TYPE
        )
    }
}
