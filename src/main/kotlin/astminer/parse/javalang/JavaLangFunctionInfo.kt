package astminer.parse.javalang

import astminer.common.model.EnclosingElement
import astminer.common.model.FunctionInfo
import astminer.common.model.FunctionInfoParameter
import astminer.common.model.SimpleNode
import mu.KotlinLogging

private val logger = KotlinLogging.logger("JavaLang-Function-info")

class JavaLangFunctionInfo(override val root: SimpleNode, override val filePath: String) : FunctionInfo<SimpleNode> {
    override val nameNode: SimpleNode? = root.getChildOfType(NAME)

    override val body: SimpleNode? = root.getChildOfType(BODY)

    override val annotations: List<String>?
        get() = super.annotations
    override val modifiers: List<String>?
        get() = super.modifiers

    //TODO: refactor when function `extractWithLogger` will be introduced
    override val parameters: List<FunctionInfoParameter>? = try {
        run {
            val parameters = root.getChildOfType(PARAMETERS) ?: return@run listOf<FunctionInfoParameter>()
            parameters.children.map { parameter ->
                val type = parameter.children.find { it.typeLabel in possibleTypes }?.extractType()
                checkNotNull(type) { "Can't extract parameter type" }
                val name = parameter.children.find { it.typeLabel == NAME }?.originalToken
                checkNotNull(name) { "Can't find parameter name" }
                return@map FunctionInfoParameter(name, type)
            }
        }
    } catch (e: IllegalStateException) {
        logger.warn { e.message + " in function $name in $filePath" }
        null
    }

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

        const val PARAMETERS = "parameters"
    }
}
