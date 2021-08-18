package astminer.parse.spoon

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy

class SpoonJavaFunctionInfo(override val root: SpoonNode, override val filePath: String) : FunctionInfo<SpoonNode> {
    override val nameNode: SpoonNode = root

    override val parameters: List<FunctionInfoParameter> =
        root.preOrder().filter { it.typeLabel == PARAMETER_TYPE }.map { assembleParameter(it) }

    override val annotations: List<String>? = run {
        root.getChildrenOfType(ANNOTATION_NODE_TYPE).map {
            return@map it.getChildOfType(TYPE_REFERENCE)?.originalToken ?: return@run null
        }
    }

    override val returnType: String? = root.children.find { it.typeLabel in POSSIBLE_PARAMETER_TYPES }?.originalToken

    override val body: SpoonNode? = root.getChildOfType(BLOCK)

    override val isConstructor: Boolean = false

    override val enclosingElement: EnclosingElement<SpoonNode>? =
        root.findEnclosingElementBy { it.typeLabel == CLASS_DECLARATION_TYPE }?.assembleEnclosingClass()

    private fun assembleParameter(parameterNode: SpoonNode): FunctionInfoParameter {
        val type = parameterNode.children.find { it.typeLabel in POSSIBLE_PARAMETER_TYPES }?.originalToken
        val name = parameterNode.originalToken
        checkNotNull(name) { "Couldn't find parameter name token" }
        return FunctionInfoParameter(name, type)
    }

    private fun SpoonNode.assembleEnclosingClass(): EnclosingElement<SpoonNode> {
        return EnclosingElement(
            type = EnclosingElementType.Class,
            name = this.originalToken,
            root = this
        )
    }

    companion object {
        const val PARAMETER_TYPE = "Parameter"
        private const val TYPE_REFERENCE = "TypeReference"
        private const val ARRAY_TYPE_REFERENCE = "ArrayTypeReference"
        val POSSIBLE_PARAMETER_TYPES = listOf(TYPE_REFERENCE, ARRAY_TYPE_REFERENCE)
        const val CLASS_DECLARATION_TYPE = "Class"
        const val ANNOTATION_NODE_TYPE = "Annotation"
        const val BLOCK = "Block"
    }
}
