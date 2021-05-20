package astminer.parse.antlr

import astminer.common.model.Node
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Vocabulary
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.TerminalNode

fun convertAntlrTree(tree: ParserRuleContext, ruleNames: Array<String>, vocabulary: Vocabulary): AntlrNode {
    return compressTree(convertRuleContext(tree, ruleNames, null, vocabulary))
}

private fun convertRuleContext(ruleContext: ParserRuleContext, ruleNames: Array<String>, parent: Node?, vocabulary: Vocabulary): AntlrNode {
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
    currentNode.setChildren(children)

    return currentNode
}

private fun convertTerminal(terminalNode: TerminalNode, parent: Node?, vocabulary: Vocabulary): AntlrNode {
    return AntlrNode(vocabulary.getSymbolicName(terminalNode.symbol.type), parent, terminalNode.symbol.text)
}

private fun convertErrorNode(errorNode: ErrorNode, parent: Node?): AntlrNode {
    return AntlrNode("Error", parent, errorNode.text)
}

/**
 * Remove intermediate nodes that have a single child.
 */
fun simplifyTree(tree: AntlrNode): AntlrNode {
    return if (tree.getChildren().size == 1) {
        simplifyTree(tree.getChildren().first())
    } else {
        tree.setChildren(tree.getChildren().map { simplifyTree(it) }.toMutableList())
        tree
    }
}

/**
 * Compress paths of intermediate nodes that have a single child into individual nodes.
 */
fun compressTree(root: AntlrNode): AntlrNode {
    return if (root.getChildren().size == 1) {
        val child = compressTree(root.getChildren().first())
        val compressedNode = AntlrNode(
                root.getTypeLabel() + "|" + child.getTypeLabel(),
                root.getParent(),
                child.getToken()
        )
        compressedNode.setChildren(child.getChildren())
        compressedNode
    } else {
        root.setChildren(root.getChildren().map { compressTree(it) }.toMutableList())
        root
    }
}


fun decompressTypeLabel(typeLabel: String) = typeLabel.split("|")

fun AntlrNode.lastLabel() = decompressTypeLabel(getTypeLabel()).last()

fun AntlrNode.firstLabel() = decompressTypeLabel(getTypeLabel()).first()

fun AntlrNode.hasLastLabel(label: String): Boolean = lastLabel() == label

fun AntlrNode.lastLabelIn(labels: List<String>): Boolean = labels.contains(lastLabel())

fun AntlrNode.hasFirstLabel(label: String): Boolean = firstLabel() == label

fun AntlrNode.firstLabelIn(labels: List<String>): Boolean = labels.contains(firstLabel())

fun Node.getTokensFromSubtree(): String {
    if (isLeaf()) {
        return getToken()
    }
    return getChildren().joinToString(separator = "") { child ->
        child.getTokensFromSubtree()
    }
}

fun AntlrNode.getItOrChildrenOfType(typeLabel: String) : List<AntlrNode> {
    return if (hasLastLabel(typeLabel)) {
        listOf(this)
    } else {
        this.getChildrenOfType(typeLabel).map { it }
    }
}
