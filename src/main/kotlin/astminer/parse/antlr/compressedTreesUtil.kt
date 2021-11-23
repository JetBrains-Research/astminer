package astminer.parse.antlr

import astminer.common.model.Node

fun decompressTypeLabel(typeLabel: String) = typeLabel.split("|")

inline fun <reified T: Node> T.traverseDown(): T {
    var curNode: Node = this
    while (curNode.children.size == 1) { curNode = curNode.children.first()}
    return curNode as T
}

inline fun <reified T: Node> T.traverseUp(): T {
    var curNode: Node? = this
    while (curNode?.parent?.children?.size == 1) { curNode = curNode.parent}
    return curNode as T
}

fun AntlrNode.lastLabel() = traverseDown().typeLabel

fun AntlrNode.firstLabel(): String = traverseUp().typeLabel

fun AntlrNode.hasLastLabel(label: String): Boolean = lastLabel() == label

fun AntlrNode.lastLabelIn(labels: List<String>): Boolean = labels.contains(lastLabel())

fun AntlrNode.hasFirstLabel(label: String): Boolean = firstLabel() == label

fun AntlrNode.firstLabelIn(labels: List<String>): Boolean = labels.contains(firstLabel())

fun Node.getTokensFromSubtree(): String =
    if (isLeaf()) token.original ?: "" else children.joinToString(separator = "") { it.getTokensFromSubtree() }

fun AntlrNode.getItOrChildrenOfType(typeLabel: String): List<AntlrNode> =
    if (hasLastLabel(typeLabel)) listOf(this) else this.getChildrenOfType(typeLabel)
