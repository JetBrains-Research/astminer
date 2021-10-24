package astminer.parse.spoon

import astminer.common.model.EnclosingElement
import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.parse.findEnclosingElementBy
import mu.KotlinLogging

private val logger = KotlinLogging.logger("Spoon-Java-function-info")

class SpoonJavaFunctionInfo(override val root: SpoonNode, override val filePath: String) : FunctionInfo<SpoonNode> {
    override val nameNode: SpoonNode = root

    override val parameters: List<FunctionInfoParameter>? = run {
        root.preOrder().filter { it.typeLabel == PARAMETER_TYPE }.map {
            try { assembleParameter(it) } catch (e: IllegalStateException) {
                logger.warn { e.message }
                return@run null
            }
        }
    }

    override val annotations: List<String>? = run {
        root.getChildrenOfType(ANNOTATION_NODE_TYPE).map {
            it.getChildOfType(TYPE_REFERENCE)?.token?.original ?: return@run null
        }
    }

    override val returnType: String? = root.children.find { it.typeLabel in POSSIBLE_PARAMETER_TYPES }?.token?.original

    override val body: SpoonNode? = root.getChildOfType(BLOCK)

    override val isConstructor: Boolean = false

    override val enclosingElement: EnclosingElement<SpoonNode>? =
        root.findEnclosingElementBy { it.typeLabel in POSSIBLE_ENCLOSING_ELEMENTS }?.assembleEnclosingClass()

    private fun assembleParameter(parameterNode: SpoonNode): FunctionInfoParameter {
        val type = parameterNode.children.find { it.typeLabel in POSSIBLE_PARAMETER_TYPES }?.token?.original
        val name = parameterNode.token.original
        checkNotNull(name) { "Couldn't find parameter name token" }
        return FunctionInfoParameter(name, type)
    }

    private fun SpoonNode.assembleEnclosingClass(): EnclosingElement<SpoonNode>? = extractWithLogger(logger) {
        val type = when (this.typeLabel) {
            ENUM_DECLARATION_TYPE -> EnclosingElementType.Enum
            CLASS_DECLARATION_TYPE -> EnclosingElementType.Class
            else -> error("Can't find any enclosing type association")
        }
        EnclosingElement(type, this.token.original, root)
    }

    companion object {
        const val PARAMETER_TYPE = "Parameter"
        private const val TYPE_REFERENCE = "TypeReference"
        private const val ARRAY_TYPE_REFERENCE = "ArrayTypeReference"
        val POSSIBLE_PARAMETER_TYPES = listOf(TYPE_REFERENCE, ARRAY_TYPE_REFERENCE)
        const val CLASS_DECLARATION_TYPE = "Class"
        const val ENUM_DECLARATION_TYPE = "Enum"
        val POSSIBLE_ENCLOSING_ELEMENTS = listOf(CLASS_DECLARATION_TYPE, ENUM_DECLARATION_TYPE)
        const val ANNOTATION_NODE_TYPE = "Annotation"
        const val BLOCK = "Block"
    }
}
