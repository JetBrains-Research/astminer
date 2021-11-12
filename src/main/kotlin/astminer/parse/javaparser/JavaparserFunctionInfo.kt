package astminer.parse.javaparser

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("JavaParser-function-info")

class JavaparserFunctionInfo(override val root: JavaParserNode, override val filePath: String) :
    FunctionInfo<JavaParserNode> {
    override val nameNode: JavaParserNode? =
        root.getChildOfType(METHOD_NAME)

    override val parameters: List<FunctionInfoParameter>? = run {
        root.children.filter { it.typeLabel == PARAMETER }.map {
            try {
                assembleParameter(it)
            } catch (e: IllegalStateException) {
                logger.warn { "In function $name in file $filePath ${e.message}" }
                return@run null
            }
        }
    }

    override val modifiers: List<String>? = run {
        root.children.filter { it.typeLabel == MODIFIER }.map {
            val token = it.token.original
            if (token == null) {
                logger.warn { "Modifier for function $name in file $filePath doesn't have a token" }
                return@run null
            }
            return@map token
        }
    }

    override val annotations: List<String>? = run {
        root.children.filter { it.typeLabel in POSSIBLE_ANNOTATION_TYPES }.map {
            val token = it.getChildOfType(ANNOTATION_NAME)?.token?.original?.split(".")?.last()
            if (token == null) {
                logger.warn { "Annotation for function $name in file $filePath doesn't have a token" }
                return@run null
            }
            return@map token
        }
    }

    override val enclosingElement: EnclosingElement<JavaParserNode>? =
        root.findEnclosingElementBy { it.typeLabel in possibleEnclosingElements }?.assembleEnclosingClass()

    override val body: JavaParserNode? = root.getChildOfType(FUNCTION_BODY)

    override val isConstructor: Boolean = false

    private fun assembleParameter(node: JavaParserNode): FunctionInfoParameter =
        FunctionInfoParameter(type = getParameterType(node), name = getParameterName(node))

    private fun getParameterType(node: JavaParserNode): String {
        val possibleTypeNode = node.children.find { it.typeLabel in POSSIBLE_PARAMETERS_TYPES }
        checkNotNull(possibleTypeNode) { "Couldn't find parameter type node" }
        val typeToken = when (possibleTypeNode.typeLabel) {
            ARRAY_TYPE -> getParameterType(possibleTypeNode) + ARRAY_BRACKETS
            PRIMITIVE_TYPE -> possibleTypeNode.token.original
            CLASS_OR_INTERFACE_TYPE -> possibleTypeNode.getChildOfType(CLASS_NAME)?.token?.original
            else -> null
        }
        checkNotNull(typeToken) { "Couldn't extract parameter type from node" }
        return typeToken
    }

    private fun getParameterName(node: JavaParserNode): String {
        val name = checkNotNull(node.getChildOfType(PARAMETER_NAME)?.token?.original) { "Couldn't find parameter name" }
        return name.replace(ARRAY_BRACKETS_REGEX, "")
    }

    private fun JavaParserNode.assembleEnclosingClass(): EnclosingElement<JavaParserNode>? = extractWithLogger(logger) {
        val name = this.getChildOfType(CLASS_NAME)?.token?.original
        val type = when (this.typeLabel) {
            CLASS_OR_INTERFACE_DECLARATION -> EnclosingElementType.Class
            ENUM_DECLARATION -> EnclosingElementType.Enum
            else -> error("Can't find any enclosing type association")
        }
        EnclosingElement(type, name, this)
    }

    companion object {
        const val METHOD_NAME = "SimpleName"

        const val PRIMITIVE_TYPE = "Prim"

        const val PARAMETER = "Prm"
        const val PARAMETER_NAME = "SimpleName"
        const val PARAMETER_ANNOTATION = "MarkerExpr"

        const val CLASS_OR_INTERFACE_TYPE = "Cls"
        const val CLASS_OR_INTERFACE_DECLARATION = "ClsD"
        const val CLASS_NAME = "SimpleName"

        const val ENUM_DECLARATION = "EnD"

        val possibleEnclosingElements = listOf(CLASS_OR_INTERFACE_DECLARATION, ENUM_DECLARATION)

        const val ARRAY_TYPE = "ArTy"
        const val ARRAY_BRACKETS = "[]"
        val ARRAY_BRACKETS_REGEX = "\\[\\]".toRegex()

        val POSSIBLE_PARAMETERS_TYPES = listOf(PRIMITIVE_TYPE, CLASS_OR_INTERFACE_TYPE, ARRAY_TYPE)
        val POSSIBLE_ANNOTATION_TYPES = listOf("SMEx", "MarkerExpr", "NormEx")
        const val ANNOTATION_NAME = "Name"

        const val MODIFIER = "Modifier"
        const val FUNCTION_BODY = "Bk"
    }
}
