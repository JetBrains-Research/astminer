package astminer.parse.gumtree

import astminer.common.model.ParseResult
import astminer.parse.HandlerFactory
import astminer.parse.LanguageHandler
import astminer.parse.gumtree.java.GumTreeJavaNode
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.parse.gumtree.java.GumTreeJavaMethodSplitter
import astminer.parse.gumtree.python.GumTreePythonMethodSplitter
import astminer.parse.gumtree.python.GumTreePythonNode
import astminer.parse.gumtree.python.GumTreePythonParser
import java.io.File

object JavaGumtreeHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreeJavaNode> = JavaGumtreeHandler(file)

    class JavaGumtreeHandler(file: File) : LanguageHandler<GumTreeJavaNode>() {
        override val splitter = GumTreeJavaMethodSplitter()
        override val parseResult: ParseResult<GumTreeJavaNode> = GumTreeJavaParser().parseFile(file)
    }
}

object PythonGumTreeHandlerFactory : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<GumTreePythonNode> = PythonGumTreeHandler(file)

    class PythonGumTreeHandler(file: File) :  LanguageHandler<GumTreePythonNode>() {
        override val splitter = GumTreePythonMethodSplitter()
        override val parseResult: ParseResult<GumTreePythonNode> = GumTreePythonParser().parseFile(file)
    }
}