package astminer.parse.antlr

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
                child.token
        )
        compressedNode.replaceChildren(child.children)
        compressedNode
    } else {
        root.replaceChildren(root.children.map { compressTree(it) }.toMutableList())
        root
    }
}


fun decompressTypeLabel(typeLabel: String) = typeLabel.split("|")
