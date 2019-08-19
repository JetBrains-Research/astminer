package astminer.parse.java

import com.github.gumtreediff.client.Run
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator
import com.github.gumtreediff.tree.TreeContext
import astminer.common.Parser
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader

class GumTreeJavaParser : Parser<GumTreeJavaNode> {
    init {
        Run.initGenerators()
    }

    override fun parse(content: InputStream): GumTreeJavaNode? {
        val treeContext = JdtTreeGenerator().generate(InputStreamReader(content))
        return wrapGumTreeNode(treeContext)
    }

    override fun parseProject(projectRoot: File, getFilesToParse: (File) -> List<File>): List<GumTreeJavaNode?> {
        return getFilesToParse(projectRoot).map { parse(it.inputStream()) }
    }
}

fun wrapGumTreeNode(treeContext: TreeContext): GumTreeJavaNode {
    return GumTreeJavaNode(treeContext.root, treeContext, null)
}