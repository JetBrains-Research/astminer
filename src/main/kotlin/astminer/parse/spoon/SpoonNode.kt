package astminer.parse.spoon

import astminer.common.model.Node
import astminer.common.model.Token
import astminer.common.model.TokenRange
import spoon.reflect.code.*
import spoon.reflect.declaration.CtElement
import spoon.reflect.declaration.CtNamedElement
import spoon.reflect.reference.CtReference

class SpoonNode(el: CtElement, override val parent: SpoonNode?) : Node(el.getSpoonToken()) {
    // Turning Ct<Something>Impl -> <Something>
    override val typeLabel = el.javaClass.simpleName.substring(startIndex = 2).dropLast(4)

    override val children = run { el.directChildren.map { SpoonNode(it, this) } }.toMutableList()

    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun getChildOfType(typeLabel: String): SpoonNode? =
        super.getChildOfType(typeLabel) as? SpoonNode

    override fun getChildrenOfType(typeLabel: String): List<SpoonNode> =
        super.getChildrenOfType(typeLabel).map { it as SpoonNode }

    override fun preOrder(): List<SpoonNode> = super.preOrder().map { it as SpoonNode }
    override fun postOrder(): List<SpoonNode> = super.postOrder().map { it as SpoonNode }
}

private fun CtElement.getSpoonToken(): Token {
    val originalToken = when {
        this is CtNamedElement -> this.simpleName
        this is CtVariableAccess<*> -> this.variable.simpleName
        this is CtInvocation<*> -> this.executable?.simpleName
        this is CtOperatorAssignment<*, *> -> this.label
        this is CtTypeAccess<*> -> this.accessedType?.qualifiedName
        this is CtReference -> this.simpleName
        this is CtBinaryOperator<*> -> this.kind.toString()
        this is CtUnaryOperator<*> -> this.kind.toString()
        this is CtLiteral<*> -> this.toString()
        this.directChildren.size == 0 -> this.toString()
        else -> null
    }
    val range = if (this.position.isValidPosition) {
        try {
            TokenRange(
                this.position.line to this.position.column,
                this.position.endLine to this.position.endColumn
            )
        } catch (e: NullPointerException) {
            null
        }
    } else null
    return Token(originalToken, range)
}
