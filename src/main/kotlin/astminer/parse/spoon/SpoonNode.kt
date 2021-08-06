package astminer.parse.spoon

import astminer.common.model.Node
import spoon.reflect.code.CtBinaryOperator
import spoon.reflect.code.CtLiteral
import spoon.reflect.code.CtUnaryOperator
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtNamedElement
import spoon.reflect.reference.CtReference

class SpoonNode(el: CtElement, override val parent: SpoonNode?) : Node(el.getSpoonValue()) {
    val roleInParent: String? = el.roleInParent?.toString()

    // Turning Ct<Something> -> <Something>
    override val typeLabel = el.javaClass.simpleName.substring(startIndex = 2)

    override val children = run { el.directChildren.map { SpoonNode(it, this) } }.toMutableList()

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun preOrder(): List<SpoonNode> = super.preOrder().map { it as SpoonNode }
    override fun postOrder(): List<SpoonNode> = super.postOrder().map { it as SpoonNode }

    fun getChildWithRole(role: String): SpoonNode? = children.find { it.roleInParent == role }
    fun getChildrenWithRole(role: String): List<SpoonNode> = children.filter { it.roleInParent == role }
}

private fun CtElement.getSpoonValue():String? {
    return when {
        this is CtNamedElement -> this.simpleName
        this is CtReference -> this.simpleName
        this is CtBinaryOperator<*> -> this.kind.toString()
        this is CtUnaryOperator<*> -> this.kind.toString()
        this.directChildren.size == 0 -> this.toString()

        /* For some reason not every literal in spoon have value */
        this is CtLiteral<*> -> try { this.value.toString() } catch (e: NullPointerException) {
            logger.warn { "Literal without value found : ${e.message}" }
            require(this.directChildren.isNotEmpty()) { "Literal leaf does not have a value" }
            null
        }

        else -> null
    }
}