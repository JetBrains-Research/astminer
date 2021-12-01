package astminer.storage

import astminer.common.EMPTY_TOKEN
import astminer.common.SimpleNode
import astminer.common.TOKEN_DELIMITER
import astminer.common.model.Node

const val GENERATED_NODE = "<G>"

/**
 * Compress every consecutive nodes that has 1 child into one node.
 * Type labels will be concatenated with `TOKEN_DELIMITER`.
 * If node with non-empty token will be compressed, new node with
 * the same token will be created to avoid information loss.
 */
fun Node.structurallyNormalized() = this.extractToken().compressTree()

/**
 * Extracts tokens from non-leaf nodes into separate children
 * by converting the tree into a simple form.
 */
private fun Node.extractToken(parent: SimpleNode? = null): SimpleNode {
    if (isLeaf()) {
        val new = SimpleNode(typeLabel, mutableListOf(), parent, range, token.original)
        new.token.technical = token.technical
        return new
    }
    val new = SimpleNode(typeLabel, mutableListOf(), parent, range, null)
    if (token.final() != EMPTY_TOKEN) {
        val generated = SimpleNode(GENERATED_NODE, mutableListOf(), new, range, token.original)
        generated.token.technical = token.technical
        new.children.add(generated)
    }
    new.children.addAll(this.children.map { it.extractToken(new) })
    return new
}

private fun SimpleNode.compressTree(): SimpleNode {
    val bamboo = bambooBranch()
    if (bamboo.isEmpty()) { return this.also { children.replaceAll { it.compressTree() } } }
    val newLabel = bamboo.joinToString(TOKEN_DELIMITER) { it.typeLabel }
    // we don't need lastOrNull because bamboo is not empty
    val bambooEnd = bamboo.last()
    val newToken = bambooEnd.token.original
    val newNode = SimpleNode(newLabel, mutableListOf(), parent, range, newToken)
    newNode.token.technical = bambooEnd.token.technical
    newNode.children.addAll(bambooEnd.children.map { (it as SimpleNode).compressTree() })
    return newNode
}

private fun Node.bambooBranch(): List<Node> {
    var curNode: Node = this
    val branch = mutableListOf<Node>()
    while (curNode.isBamboo()) {
        branch.add(curNode)
        curNode = curNode.children.first()
        if (curNode.isLeaf()) {
            branch.add(curNode)
            break
        }
    }
    return branch
}

private fun Node.isBamboo() = this.children.size == 1
