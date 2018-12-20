package miningtool.parse.antlr

import miningtool.common.Node
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.TerminalNode

fun convertAntlrTree(tree: ParserRuleContext, ruleNames: Array<String>): SimpleNode {
    return simplifyTree(convertRuleContext(tree, ruleNames, null))
}

private fun convertRuleContext(ruleContext: ParserRuleContext, ruleNames: Array<String>, parent: Node?): SimpleNode {
    val typeLabel = ruleNames[ruleContext.ruleIndex]
    val currentNode = SimpleNode(typeLabel, parent, null)
    val children: MutableList<Node> = ArrayList()

    ruleContext.children.forEach {
        if (it is TerminalNode) {
            children.add(convertTerminal(it, currentNode))
            return@forEach
        }
        if (it is ErrorNode) {
            children.add(convertErrorNode(it, currentNode))
            return@forEach
        }
        children.add(convertRuleContext(it as ParserRuleContext, ruleNames, currentNode))
    }
    currentNode.setChildren(children)

    return currentNode
}

private fun convertTerminal(terminalNode: TerminalNode, parent: Node?): SimpleNode {
    return SimpleNode("Terminal", parent, terminalNode.symbol.text)
}

private fun convertErrorNode(errorNode: ErrorNode, parent: Node?): SimpleNode {
    return SimpleNode("Error", parent, errorNode.text)
}

private fun simplifyTree(tree: SimpleNode): SimpleNode {
    return if (tree.getChildren().size == 1) {
        simplifyTree(tree.getChildren().first() as SimpleNode)
    } else {
        tree.setChildren(tree.getChildren().map { simplifyTree(it as SimpleNode) })
        tree
    }
}

