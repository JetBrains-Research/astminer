package astminer.parse.gumtree.java.srcML

import astminer.common.model.Parser
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.PositionConverter
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.srcml.SrcmlJavaTreeGenerator
import java.io.InputStream

class GumTreeJavaSrcmlParser : Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode {
        val contentAsString = content.bufferedReader().use { it.readText() }
        val converter = PositionConverter(contentAsString)
        val reader = contentAsString.reader()
        val treeContext = SrcmlJavaTreeGenerator().generate(reader)
        return GumTreeNode(treeContext.root, converter)
    }
}
