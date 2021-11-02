package astminer.parse.javalang

import astminer.common.SimpleNode
import astminer.common.model.*
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("JavaLang-Function-info")

class JavaLangFunctionInfo(override val root: SimpleNode, override val filePath: String) : FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(NAME)

    override val body: SimpleNode? = root.getChildOfType(BODY)

    override val annotations: List<String>? = extractWithLogger(logger) {
        val annotations = root.getChildOfType(ANNOTATIONS) ?: return@extractWithLogger listOf<String>()
        annotations.children
            .map { it.getChildOfType(NAME)?.token?.original }
            .map { checkNotNull(it) { "No name for annotation found" } }
    }

    override val modifiers: List<String>? = extractWithLogger(logger) {
        val modifiers = root.getChildOfType(MODIFIERS) ?: return@extractWithLogger listOf<String>()
        modifiers.children
            .map { it.token.original }
            .map { checkNotNull(it) { "No name for modifier found" } }
    }

    override val parameters: List<FunctionInfoParameter>? = extractWithLogger(logger) {
        val parameters = root.getChildOfType(PARAMETERS) ?: return@extractWithLogger listOf<FunctionInfoParameter>()
        parameters.children.map { parameter ->
            val type = parameter.children.find { it.typeLabel in possibleTypes }?.extractType()
            checkNotNull(type) { "Can't extract parameter type" }
            val name = parameter.children.find { it.typeLabel == NAME }?.token?.original
            checkNotNull(name) { "Can't find parameter name" }
            return@map FunctionInfoParameter(name, type)
        }
    }

    override val returnType: String = root.children.find { it.typeLabel in possibleTypes }?.extractType() ?: VOID

    override val enclosingElement: EnclosingElement<SimpleNode>? = extractWithLogger(logger) {
        val enclosingNode = root.findEnclosingElementBy { it.typeLabel in possibleEnclosingElements }
            ?: return@extractWithLogger null
        val type = when (enclosingNode.typeLabel) {
            CLASS_DECLARATION -> EnclosingElementType.Class
            ENUM_DECLARATION -> EnclosingElementType.Enum
            else -> error("No type can be associated with enclosing node type label")
        }
        val name = enclosingNode.getChildOfType(NAME)?.token?.original
        EnclosingElement(type, name, enclosingNode)
    }

    override val isConstructor: Boolean = false

    private fun SimpleNode.extractType(): String = this.preOrder()
        .mapNotNull { if (it.typeLabel == "dimensions" && it.isLeaf()) "[]" else it.token.original }
        .joinToString(separator = "")

    companion object {
        const val NAME = "name"
        const val BODY = "body"

        const val VOID = "void"
        val possibleTypes = listOf(
            "BasicType",
            "ReferenceType"
        )

        const val PARAMETERS = "parameters"
        const val MODIFIERS = "modifiers"
        const val ANNOTATIONS = "annotations"

        const val CLASS_DECLARATION = "ClassDeclaration"
        const val ENUM_DECLARATION = "EnumDeclaration"

        val possibleEnclosingElements = listOf(
            CLASS_DECLARATION,
            ENUM_DECLARATION
        )
    }
}
