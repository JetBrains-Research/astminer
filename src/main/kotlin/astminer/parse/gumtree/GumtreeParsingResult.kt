package astminer.parse.gumtree

import astminer.common.model.ParsingResult
import astminer.common.model.ParsingResultFactory
import astminer.parse.gumtree.java.GumTreeJavaFunctionSplitter
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.parse.gumtree.python.GumTreePythonFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonParser
import java.io.File

object GumtreeJavaParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<GumTreeNode> = JavaGumtreeParsingResult(file)

    class JavaGumtreeParsingResult(file: File) : ParsingResult<GumTreeNode>(file) {
        override val root = GumTreeJavaParser().parseFile(file)
        override val splitter = GumTreeJavaFunctionSplitter()
    }
}

object GumtreePythonParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<GumTreeNode> = PythonGumtreeParsingResult(file)

    class PythonGumtreeParsingResult(file: File) : ParsingResult<GumTreeNode>(file) {
        override val root = GumTreePythonParser().parseFile(file)
        override val splitter = GumTreePythonFunctionSplitter()
    }
}
