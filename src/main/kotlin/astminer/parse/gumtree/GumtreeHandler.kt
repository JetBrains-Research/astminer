package astminer.parse.gumtree

import astminer.common.model.*
import astminer.parse.gumtree.java.JDT.GumTreeJavaJDTParser
import astminer.parse.gumtree.java.JDT.GumTreeJavaJDTFunctionSplitter
import astminer.parse.gumtree.java.srcML.GumTreeJavaSrcmlParser
import astminer.parse.gumtree.java.srcML.GumTreeSrcmlFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonParser
import java.io.File

object GumTreeJavaSrcmlHandlerFactory: HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<out Node> = GumTreeJavaSrcmlHandler(file)

    class GumTreeJavaSrcmlHandler(file: File) : LanguageHandler<GumTreeNode>() {
        override val parseResult: ParseResult<GumTreeNode> = GumTreeJavaSrcmlParser().parseFile(file)
        override val splitter: TreeFunctionSplitter<GumTreeNode> = GumTreeSrcmlFunctionSplitter()
    }
}

object GumtreeJavaJDTHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreeNode> = GumtreeJavaJDTHandler(file)

    class GumtreeJavaJDTHandler(file: File) : LanguageHandler<GumTreeNode>() {
        override val splitter = GumTreeJavaJDTFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreeJavaJDTParser().parseFile(file)
    }
}

object GumtreePythonHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreeNode> = PythonGumTreeHandler(file)

    class PythonGumTreeHandler(file: File) :  LanguageHandler<GumTreeNode>() {
        override val splitter = GumTreePythonFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreePythonParser().parseFile(file)
    }
}