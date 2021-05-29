package astminer.parse.antlr

import astminer.common.DEFAULT_TOKEN
import astminer.common.model.Node
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Vocabulary
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.TerminalNode

fun convertAntlrTree(tree: ParserRuleContext, ruleNames: Array<String>, vocabulary: Vocabulary): AntlrNode {
    return compressTree(convertRuleContext(tree, ruleNames, null, vocabulary))
}

private fun convertRuleContext(ruleContext: ParserRuleContext, ruleNames: Array<String>, parent: AntlrNode?, vocabulary: Vocabulary): AntlrNode {
    val typeLabel = ruleNames[ruleContext.ruleIndex]
    val currentNode = AntlrNode(typeLabel, parent, null)
    val children: MutableList<AntlrNode> = ArrayList()

    ruleContext.children?.forEach {
        if (it is TerminalNode) {
            children.add(convertTerminal(it, currentNode, vocabulary))
            return@forEach
        }
        if (it is ErrorNode) {
            children.add(convertErrorNode(it, currentNode))
            return@forEach
        }
        children.add(convertRuleContext(it as ParserRuleContext, ruleNames, currentNode, vocabulary))
    }
    currentNode.replaceChildren(children)

    return currentNode
}

private fun convertTerminal(terminalNode: TerminalNode, parent: AntlrNode?, vocabulary: Vocabulary): AntlrNode {
    return AntlrNode(vocabulary.getSymbolicName(terminalNode.symbol.type), parent, terminalNode.symbol.text)
}

private fun convertErrorNode(errorNode: ErrorNode, parent: AntlrNode?): AntlrNode {
    return AntlrNode("Error", parent, errorNode.text)
}

/**
 * Remove intermediate nodes that have a single child.
 */
fun simplifyTree(tree: AntlrNode): AntlrNode {
    return if (tree.children.size == 1) {
        simplifyTree(tree.children.first())
    } else {
        tree.replaceChildren(tree.children.map { simplifyTree(it) }.toMutableList())
        tree
    }
}

/**
 * Compress paths of intermediate nodes that have a single child into individual nodes.
 */
fun compressTree(root: AntlrNode): AntlrNode {
    return if (root.children.size == 1) {
        val child = compressTree(root.children.first())
        val compressedNode = AntlrNode(
                root.typeLabel + "|" + child.typeLabel,
                root.parent,
                child.originalToken
        )
        compressedNode.replaceChildren(child.children)
        compressedNode
    } else {
        root.replaceChildren(root.children.map { compressTree(it) }.toMutableList())
        root
    }
}


fun decompressTypeLabel(typeLabel: String) = typeLabel.split("|")

fun AntlrNode.lastLabel() = decompressTypeLabel(typeLabel).last()

fun AntlrNode.firstLabel() = decompressTypeLabel(typeLabel).first()

fun AntlrNode.hasLastLabel(label: String): Boolean = lastLabel() == label

fun AntlrNode.lastLabelIn(labels: List<String>): Boolean = labels.contains(lastLabel())

fun AntlrNode.hasFirstLabel(label: String): Boolean = firstLabel() == label

fun AntlrNode.firstLabelIn(labels: List<String>): Boolean = labels.contains(firstLabel())

fun Node.getTokensFromSubtree(): String =
    if (isLeaf()) originalToken ?: DEFAULT_TOKEN
    else children.joinToString(separator = "") { child -> child.getTokensFromSubtree() }

fun AntlrNode.getItOrChildrenOfType(typeLabel: String) : List<AntlrNode> =
    if (hasLastLabel(typeLabel)) listOf(this)
    else this.getChildrenOfType(typeLabel).map { it }
