package astminer.parse.treesitter.java

import astminer.common.EMPTY_TOKEN
import astminer.common.SimpleNode
import astminer.common.model.*
import astminer.parse.antlr.getTokensFromSubtree
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("TreeSitter-Java-FunctionInfo")

class TreeSitterJavaFunctionInfo(override val root: SimpleNode, override val filePath: String) :
    FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(NAME)

    override val body: SimpleNode? = root.getChildOfType(BODY)

    override val annotations: List<String>? = extractWithLogger(logger) {
        // In tree sitter java grammar annotations are children of modifiers node
        val annotations = root.getChildOfType(MODIFIERS) ?: return@extractWithLogger listOf()
        annotations.children
            .filter { it.typeLabel in possibleAnnotations }
            .map { annotation -> annotation.preOrder().filter { it.typeLabel in listOf(NAME, SCOPE_IDENTIFIER, DOT) } }
            .map { nameNodes -> nameNodes.map { it.token.original ?: "" } }
            .map { nameNodesWithToken -> nameNodesWithToken.joinToString(separator = "") }
    }

    override val modifiers: List<String>? = extractWithLogger(logger) {
        val modifiers = root.getChildOfType(MODIFIERS) ?: return@extractWithLogger listOf<String>()
        modifiers.children
            .filter { it.typeLabel in possibleModifiers }
            .map { it.token.original }
            .map { checkNotNull(it) { "Modifier without a token" } }
    }

    override val parameters: List<FunctionInfoParameter>? = extractWithLogger(logger) {
        val parametersRoot = root.getChildOfType(PARAMETERS) ?: return@extractWithLogger listOf<FunctionInfoParameter>()
        parametersRoot.children.filter { it.typeLabel in possibleParameters }.map { parameter ->
            val possibleNameNode = parameter.getChildOfType(NAME)
            val name = if (possibleNameNode != null) {
                possibleNameNode.token.original
            } else {
                parameter.getChildOfType(VARIABLE_DECLARATOR)?.getChildOfType(NAME)?.token?.original
            }
            checkNotNull(name) { "Can't find parameter name" }

            val type = parameter.children.find { it.typeLabel in returnTypes }?.getTokensFromSubtree()
            checkNotNull(type) { "Can't assemble parameter type" }
            val dimensions = parameter.getChildOfType(ARRAY_DIMENSION)?.getTokensFromSubtree() ?: ""

            return@map FunctionInfoParameter(name, type + dimensions)
        }
    }

    override val returnType: String = run {
        val returnTypeNode = root.children.find { it.typeLabel in returnTypes }
        checkNotNull(returnTypeNode) { "No return type in function $name in file $filePath" }

        var collectedType = returnTypeNode.getTokensFromSubtree()
        if (returnTypeNode.typeLabel == ARRAY_TYPE) {
            collectedType = collectedType.replace(EMPTY_TOKEN, "[]")
        }
        return@run collectedType
    }

    override val enclosingElement: EnclosingElement<SimpleNode>? = extractWithLogger(logger) {
        val enclosingNode = root.findEnclosingElementBy { it.typeLabel in possible_enclosings }
            ?: return@extractWithLogger null
        val name = enclosingNode.getChildOfType(NAME)?.token?.original
        val type = when (enclosingNode.typeLabel) {
            CLASS_DECLARATION -> EnclosingElementType.Class
            ENUM_DECLARATION -> EnclosingElementType.Enum
            else -> error("Can't find any enclosing type association")
        }
        EnclosingElement(type, name, enclosingNode)
    }

    override val isConstructor: Boolean = false

    override fun isBlank(): Boolean = super.isBlank() || body?.getTokensFromSubtree() == "{}"

    companion object {
        const val NAME = "identifier"
        const val SCOPE_IDENTIFIER = "scope_identifier"
        const val DOT = "."
        const val BODY = "block"
        const val ARRAY_TYPE = "array_type"
        const val ARRAY_DIMENSION = "dimensions"

        const val CLASS_DECLARATION = "class_declaration"
        const val ENUM_DECLARATION = "enum_declaration"

        val possible_enclosings = listOf(
            CLASS_DECLARATION,
            ENUM_DECLARATION
        )

        const val PARAMETERS = "formal_parameters"
        const val FORMAL_PARAMETER = "formal_parameter"
        const val SPREAD_PARAMETER = "spread_parameter"
        const val RECEIVER_PARAMETER = "receiver_parameter"
        val possibleParameters = listOf(
            FORMAL_PARAMETER,
            SPREAD_PARAMETER,
            RECEIVER_PARAMETER
        )
        const val VARIABLE_DECLARATOR = "variable_declarator"

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
