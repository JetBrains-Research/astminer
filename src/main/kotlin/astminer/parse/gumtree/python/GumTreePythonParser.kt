package astminer.parse.gumtree.python

import astminer.common.model.Parser
import astminer.common.model.ParserNotInstalledException
import astminer.parse.ParsingException
import astminer.parse.gumtree.GumTreeNode
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.python.PythonTreeGenerator
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class GumTreePythonParser : Parser<GumTreeNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreeNode = try {
        val context = PythonTreeGenerator().generate(InputStreamReader(content))
        GumTreeNode(context.root)
    } catch (e: RuntimeException) {
        throw ParsingException("GumTree", "Python", e)
    } catch (e: IOException) {
        throw ParserNotInstalledException("Gumtree", "Python", e)
    }
}
