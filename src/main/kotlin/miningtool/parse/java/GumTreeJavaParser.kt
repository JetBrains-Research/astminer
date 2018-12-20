package miningtool.parse.java

import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator
import com.github.gumtreediff.tree.TreeContext
import miningtool.common.Node
import miningtool.common.Parser
import java.io.InputStream
import java.io.InputStreamReader

class GumTreeJavaParser : Parser<GumTreeJavaNode>() {
    init {
        Run.initGenerators()
    }

    override fun parse(content: InputStream): GumTreeJavaNode? {
        val treeContext = JdtTreeGenerator().generate(InputStreamReader(content))
        return wrapGumTreeNode(treeContext)
    }
}

fun wrapGumTreeNode(treeContext: TreeContext): GumTreeJavaNode {
    return GumTreeJavaNode(treeContext.root, treeContext, null)
}