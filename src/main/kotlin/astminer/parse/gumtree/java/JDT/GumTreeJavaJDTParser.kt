package astminer.parse.gumtree.java.JDT

import astminer.common.model.Parser
import astminer.parse.gumtree.GumTreeNode
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator
import com.github.gumtreediff.tree.TreeContext
import astminer.common.model.Parser
import astminer.parse.gumtree.GumTreeNode
import java.io.InputStream
import java.io.InputStreamReader

class GumTreeJavaJDTParser : Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode {
        val treeContext = JdtTreeGenerator().generate(InputStreamReader(content))
        return wrapGumTreeNode(treeContext)
    }
}
