package astminer.storage

import astminer.common.EMPTY_TOKEN
import astminer.common.SimpleNode
import astminer.common.TOKEN_DELIMITER
import astminer.common.model.Node

const val GENERATED_NODE = "<G>"

/**
 * Compress every node that has 1 child into one node.
 * Type labels will be concatenated with `TOKEN_DELIMITER`.
 * If node with non-empty token will be compressed, new node with
 * the same token will be created to avoid information loss.
 */
fun Node.compressTree(): SimpleNode {
    val bamboo = bambooBranch()
    val newLabel = bamboo.joinToString(TOKEN_DELIMITER) { it.typeLabel }
    var newToken = bamboo.lastOrNull()?.token?.original
    val newChildren = if (bamboo.isNotEmpty()) {
        bamboo.last().children.map { it.compressTree() }.toMutableList()
    } else {
        mutableListOf()
    }
    if (!isLeaf() && token.final() != EMPTY_TOKEN) {
        newChildren.add(generateDummy())
        newToken = null
    }
    val newNode = SimpleNode(
        typeLabel = newLabel,
        token = newToken,
        parent = parent,
        children = newChildren,
        range = range
    )
    newNode.token.technical = token.technical
    return newNode
}

private fun Node.generateDummy() = SimpleNode(
    typeLabel = GENERATED_NODE,
    token = token.original,
    parent = this,
    children = mutableListOf(),
    range = null,
)

private fun Node.bambooBranch(): List<Node> {
    val branch = mutableListOf(this)
    var curNode: Node = this
    while (curNode.isBamboo()) {
        curNode = curNode.children.first()
        branch.add(curNode)
    }
    return branch
}

private fun Node.isBamboo() = this.children.size == 1
