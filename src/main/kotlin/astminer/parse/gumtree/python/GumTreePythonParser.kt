package astminer.parse.gumtree.python

import astminer.common.model.Parser
import astminer.common.model.ParserNotInstalledException
import astminer.parse.ParsingException
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.PositionConverter
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.python.PythonTreeGenerator
import java.io.IOException
import java.io.InputStream

class GumTreePythonParser : Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode = try {
        val contentAsString = content.bufferedReader().use { it.readText() }
        val converter = PositionConverter(contentAsString)
        val reader = contentAsString.reader()
        val context = PythonTreeGenerator().generate(reader)
        GumTreeNode(context.root, converter)
    } catch (e: RuntimeException) {
        throw ParsingException("GumTree", "Python", e)
    } catch (e: IOException) {
        throw ParserNotInstalledException("Gumtree", "Python", e)
    }
}
