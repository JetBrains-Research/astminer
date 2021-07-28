package astminer.parse.gumtree

import astminer.common.model.Node
import astminer.common.model.ParsingResult
import astminer.common.model.ParsingResultFactory
import astminer.common.model.TreeFunctionSplitter
import astminer.parse.gumtree.java.jdt.GumTreeJavaJDTFunctionSplitter
import astminer.parse.gumtree.java.jdt.GumTreeJavaJDTParser
import astminer.parse.gumtree.java.srcML.GumTreeJavaSrcmlFunctionSplitter
import astminer.parse.gumtree.java.srcML.GumTreeJavaSrcmlParser
import astminer.parse.gumtree.python.GumTreePythonFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonParser
import java.io.File

object GumtreeJavaJDTParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<GumTreeNode> = GumTreeJavaJDTParsingResult(file)

    class GumTreeJavaJDTParsingResult(file: File) : ParsingResult<GumTreeNode>(file) {
        override val root = GumTreeJavaJDTParser().parseFile(file)
        override val splitter = GumTreeJavaJDTFunctionSplitter()
    }
}

object GumtreeJavaSrcmlParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<out Node> = GumTreeJavaSrcmlParsingResult(file)

    class GumTreeJavaSrcmlParsingResult(file: File) : ParsingResult<GumTreeNode>(file) {
        override val root: GumTreeNode = GumTreeJavaSrcmlParser().parseFile(file)
        override val splitter: TreeFunctionSplitter<GumTreeNode> = GumTreeJavaSrcmlFunctionSplitter()
    }
}

object GumtreePythonParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<GumTreeNode> = PythonGumtreeParsingResult(file)

    class PythonGumtreeParsingResult(file: File) : ParsingResult<GumTreeNode>(file) {
        override val root = GumTreePythonParser().parseFile(file)
        override val splitter = GumTreePythonFunctionSplitter()
    }
}
