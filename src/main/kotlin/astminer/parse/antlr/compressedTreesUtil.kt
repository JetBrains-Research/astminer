package astminer.parse.antlr

import astminer.common.model.Node

fun decompressTypeLabel(typeLabel: String) = typeLabel.split("|")

inline fun <reified T : Node> T.traverseDown(): T {
    var curNode: Node = this
    while (curNode.children.size == 1) { curNode = curNode.children.first() }
    return curNode as T
}

inline fun <reified T : Node> T.traverseUp(): T {
    var curNode: Node? = this
    while (curNode?.parent?.children?.size == 1) { curNode = curNode.parent }
    return curNode as T
}

fun AntlrNode.lastLabelInBamboo() = traverseDown().typeLabel

fun AntlrNode.firstLabelInBamboo(): String = traverseUp().typeLabel

fun AntlrNode.hasLastLabelInBamboo(label: String): Boolean = lastLabelInBamboo() == label

fun AntlrNode.lastLabelIn(labels: List<String>): Boolean = labels.contains(lastLabelInBamboo())

fun AntlrNode.hasFirstLabelInBamboo(label: String): Boolean = firstLabelInBamboo() == label

fun AntlrNode.firstLabelIn(labels: List<String>): Boolean = labels.contains(firstLabelInBamboo())

fun Node.getTokensFromSubtree(): String =
    if (isLeaf()) token.original ?: "" else children.joinToString(separator = "") { it.getTokensFromSubtree() }

fun AntlrNode.getItOrChildrenOfType(typeLabel: String): List<AntlrNode> =
    if (hasLastLabelInBamboo(typeLabel)) listOf(this) else this.getChildrenOfType(typeLabel)
