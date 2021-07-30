package astminer.parse.javaparser

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy

class JavaparserFunctionInfo(override val root: JavaParserNode, override val filePath: String) :
    FunctionInfo<JavaParserNode> {
    override val nameNode: JavaParserNode? =
        root.getChildOfType(METHOD_NAME)

    override val parameters: List<FunctionInfoParameter> =
        root.children.filter { it.typeLabel == PARAMETER }.map { assembleParameter(it) }

    override val enclosingElement: EnclosingElement<JavaParserNode>? =
        root.findEnclosingElementBy { it.typeLabel == CLASS_OR_INTERFACE_DECLARATION }?.assembleEnclosingClass()

    private fun assembleParameter(node: JavaParserNode): FunctionInfoParameter =
        FunctionInfoParameter(type = getParameterType(node), name = getParameterName(node))

    private fun getParameterType(node: JavaParserNode): String {
        val possibleTypeNode = node.children
            .find { it.typeLabel != PARAMETER_NAME && it.typeLabel != PARAMETER_ANNOTATION }
        checkNotNull(possibleTypeNode) { "Couldn't find parameter type node" }
        val typeToken = when (possibleTypeNode.typeLabel) {
            ARRAY_TYPE -> getParameterType(possibleTypeNode) + ARRAY_BRACKETS
            PRIMITIVE_TYPE -> possibleTypeNode.originalToken
            CLASS_OR_INTERFACE_TYPE -> possibleTypeNode.getChildOfType(CLASS_NAME)?.originalToken
            else -> null
        }
        checkNotNull(typeToken) { "Couldn't extract parameter type from node" }
        return typeToken
    }

    private fun getParameterName(node: JavaParserNode): String =
        checkNotNull(node.getChildOfType(PARAMETER_NAME)?.originalToken) { "Couldn't find parameter name" }

    private fun JavaParserNode.assembleEnclosingClass(): EnclosingElement<JavaParserNode> {
        val name = this.getChildOfType(CLASS_NAME)?.originalToken
        return EnclosingElement(
            type = EnclosingElementType.Class,
            name = name,
            root = this
        )
    }

    companion object {
        const val METHOD_NAME = "SimpleName"
        const val PARAMETER = "Prm"
        const val PARAMETER_NAME = "SimpleName"
        const val ARRAY_TYPE = "ArTy"
        const val ARRAY_BRACKETS = "[]"
        const val PRIMITIVE_TYPE = "Prim"
        const val CLASS_OR_INTERFACE_TYPE = "Cls"
        const val CLASS_OR_INTERFACE_DECLARATION = "ClsD"
        const val CLASS_NAME = "SimpleName"
        const val PARAMETER_ANNOTATION = "MarkerExpr"
    }
}
