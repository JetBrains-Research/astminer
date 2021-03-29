package astminer.parse

import astminer.common.model.ParseResult
import astminer.parse.java.GumTreeJavaNode
import astminer.parse.java.GumTreeJavaParser
import astminer.parse.java.GumTreeJavaMethodSplitter
import astminer.parse.python.GumTreePythonMethodSplitter
import astminer.parse.python.GumTreePythonNode
import astminer.parse.python.GumTreePythonParser
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