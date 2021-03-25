package astminer.parse

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.parse.java.GumTreeJavaNode
import astminer.parse.java.GumTreeJavaParser
import astminer.parse.java.GumTreeMethodSplitter
import astminer.parse.python.GumTreePythonMethodSplitter
import astminer.parse.python.GumTreePythonNode
import astminer.parse.python.GumTreePythonParser

abstract class GumTreeHandler : LanguageHandler

class JavaGumtreeHandler() : GumTreeHandler() {
    override val parser = GumTreeJavaParser()
    private val splitter = GumTreeMethodSplitter()

    override fun splitIntoMethods(root: Node): Collection<MethodInfo<out Node>> {
        require(root is GumTreeJavaNode) { "Wrong node type" }
        return splitter.splitIntoMethods(root)
    }
}

class PythonGumTreeHandler : GumTreeHandler() {
    override val parser = GumTreePythonParser()
    private val splitter = GumTreePythonMethodSplitter()
    override fun splitIntoMethods(root: Node): Collection<MethodInfo<out Node>> {
        require(root is GumTreePythonNode) { "Wrong node type" }
        return splitter.splitIntoMethods(root)
    }
}
