package astminer.parse.gumtree.java.jdt

import astminer.common.model.Parser
import astminer.parse.ParsingException
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.wrapGumTreeNode
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.SyntaxException
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator
import mu.KotlinLogging
import java.io.InputStream
import java.io.InputStreamReader

private val logger = KotlinLogging.logger("GumTree-JavaParser")

class GumTreeJavaJDTParser : Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode = try {
        val treeContext = JdtTreeGenerator().generate(InputStreamReader(content))
        wrapGumTreeNode(treeContext)
    } catch (e: SyntaxException) {
        throw ParsingException(parserType = "Gumtree", language = "Java", exc = e)
    }
}
