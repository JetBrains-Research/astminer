package astminer.parse

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.TreeMethodSplitter
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.javascript.JavaScriptMethodSplitter
import astminer.parse.antlr.javascript.JavaScriptParser
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.antlr.python.PythonParser

abstract class AntlrLanguageHandler : LanguageHandler {
    abstract val splitter: TreeMethodSplitter<SimpleNode>
    override fun splitIntoMethods(root: Node): Collection<MethodInfo<out Node>> {
        require(root is SimpleNode) { "Wrong node type" }
        return splitter.splitIntoMethods(root)
    }
}

class AntlrJavaHandler : AntlrLanguageHandler() {
    override val parser = JavaParser()
    override val splitter = JavaMethodSplitter()
}

class AntlrPythonHandler : AntlrLanguageHandler() {
    override val parser = PythonParser()
    override val splitter = PythonMethodSplitter()
}

class AntlrJavascriptHandler : AntlrLanguageHandler() {
    override val parser = JavaScriptParser()
    override val splitter = JavaScriptMethodSplitter()
}