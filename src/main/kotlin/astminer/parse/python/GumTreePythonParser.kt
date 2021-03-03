package astminer.parse.python

import astminer.common.model.Parser
import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.python.PythonTreeGenerator
import com.github.gumtreediff.tree.TreeContext
import java.io.InputStream
import java.io.InputStreamReader

class GumTreePythonParser : Parser<GumTreePythonNode> {
    init {
        Run.initGenerators()
    }

    override fun parseInputStream(content: InputStream): GumTreePythonNode {
        val context = PythonTreeGenerator().generate(InputStreamReader(content))
        return wrapGumTreeNode(context)
    }
}

fun wrapGumTreeNode(treeContext: TreeContext): GumTreePythonNode {
    return GumTreePythonNode(treeContext.root, treeContext, null)
}

