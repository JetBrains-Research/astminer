package astminer.parse.antlr

import astminer.common.model.Node

fun decompressTypeLabel(typeLabel: String) = typeLabel.split("|")

fun AntlrNode.lastLabel() = decompressTypeLabel(typeLabel).last()

fun AntlrNode.firstLabel() = decompressTypeLabel(typeLabel).first()

fun AntlrNode.hasLastLabel(label: String): Boolean = lastLabel() == label

fun AntlrNode.lastLabelIn(labels: List<String>): Boolean = labels.contains(lastLabel())

fun AntlrNode.hasFirstLabel(label: String): Boolean = firstLabel() == label

fun AntlrNode.firstLabelIn(labels: List<String>): Boolean = labels.contains(firstLabel())

fun Node.getTokensFromSubtree(): String =
    if (isLeaf()) token.original ?: "" else children.joinToString(separator = "") { it.getTokensFromSubtree() }

fun AntlrNode.getItOrChildrenOfType(typeLabel: String): List<AntlrNode> =
    if (hasLastLabel(typeLabel)) listOf(this) else this.getChildrenOfType(typeLabel)
