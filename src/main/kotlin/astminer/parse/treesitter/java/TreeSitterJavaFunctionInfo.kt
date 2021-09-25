package astminer.parse.treesitter.java

import astminer.common.EMPTY_TOKEN
import astminer.common.model.EnclosingElement
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.common.model.SimpleNode
import astminer.parse.antlr.getTokensFromSubtree

class TreeSitterJavaFunctionInfo(override val root: SimpleNode, override val filePath: String) :
    FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(NAME)

    override val body: SimpleNode? = root.getChildOfType(BODY)

    override val annotations: List<String>? =
        root.getChildOfType(MODIFIERS)?.children
            ?.filter { possibleAnnotations.contains(it.typeLabel) }
            ?.map { it.getChildOfType(NAME)?.originalToken }
            ?.map { checkNotNull(it) { "Annotation without a name in function $name in $filePath" } }

    override val modifiers: List<String>? =
        root.getChildOfType(MODIFIERS)?.children
            ?.filter { possibleModifiers.contains(it.typeLabel) }
            ?.map { it.originalToken }
            ?.map { checkNotNull(it) { "Modifier without a token in function $name in $filePath" } }

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
        const val NAME = "identifier"
        const val BODY = "block"
        const val ARRAY_TYPE = "array_type"

        const val CLASS_DECLARATION = "class_declaration"
        const val ENUM_DECLARATION = "enum_declaration"

        val possible_enclosings = listOf(
            CLASS_DECLARATION,
            ENUM_DECLARATION
        )

        const val MODIFIERS = "modifiers"

        val possibleAnnotations = listOf(
            "marker_annotation",
            "annotation"
        )

        val possibleModifiers = listOf(
            "public",
            "protected",
            "private",
            "abstract",
            "static",
            "final",
            "strictfp",
            "default",
            "synchronized",
            "native",
            "transient",
            "volatile"
        )

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
