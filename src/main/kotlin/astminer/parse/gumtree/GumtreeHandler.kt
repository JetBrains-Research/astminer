package astminer.parse.gumtree

import astminer.common.model.ParseResult
import astminer.common.model.HandlerFactory
import astminer.common.model.LanguageHandler
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.parse.gumtree.java.GumTreeJavaFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonParser
import java.io.File

object GumtreeJavaHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreeNode> = JavaGumtreeHandler(file)

    class JavaGumtreeHandler(file: File) : LanguageHandler<GumTreeNode>() {
        override val splitter = GumTreeJavaFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreeJavaParser().parseFile(file)
    }
}

object GumtreePythonHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreeNode> = PythonGumTreeHandler(file)

    class PythonGumTreeHandler(file: File) :  LanguageHandler<GumTreeNode>() {
        override val splitter = GumTreePythonFunctionSplitter()
        override val parseResult: ParseResult<GumTreeNode> = GumTreePythonParser().parseFile(file)
    }
}