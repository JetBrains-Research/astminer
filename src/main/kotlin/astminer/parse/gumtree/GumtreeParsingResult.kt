package astminer.parse.gumtree

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
    override fun parse(file: File, inputDirectoryPath: String?) =
        GumTreeJavaJDTParsingResult(file, inputDirectoryPath)

    class GumTreeJavaJDTParsingResult(file: File, inputDirectoryPath: String?) :
        ParsingResult<GumTreeNode>(file, inputDirectoryPath) {
        override val root = GumTreeJavaJDTParser().parseFile(file)
        override val splitter = GumTreeJavaJDTFunctionSplitter()
    }
}

object GumtreeJavaSrcmlParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?) =
        GumTreeJavaSrcmlParsingResult(file, inputDirectoryPath)

    class GumTreeJavaSrcmlParsingResult(file: File, inputDirectoryPath: String?) :
        ParsingResult<GumTreeNode>(file, inputDirectoryPath) {
        override val root: GumTreeNode = GumTreeJavaSrcmlParser().parseFile(file)
        override val splitter: TreeFunctionSplitter<GumTreeNode> = GumTreeJavaSrcmlFunctionSplitter()
    }
}

object GumtreePythonParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?) =
        PythonGumtreeParsingResult(file, inputDirectoryPath)

    class PythonGumtreeParsingResult(file: File, inputDirectoryPath: String?) :
        ParsingResult<GumTreeNode>(file, inputDirectoryPath) {
        override val root = GumTreePythonParser().parseFile(file)
        override val splitter = GumTreePythonFunctionSplitter()
    }
}
