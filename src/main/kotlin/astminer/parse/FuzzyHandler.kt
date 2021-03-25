package astminer.parse

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.TreeMethodSplitter
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.cpp.FuzzyNode

abstract class FuzzyHandler : LanguageHandler {
    abstract val splitter: TreeMethodSplitter<FuzzyNode>
    override fun splitIntoMethods(root: Node): Collection<MethodInfo<out Node>> {
        require(root is FuzzyNode) { "Wrong node type" }
        return splitter.splitIntoMethods(root)
    }
}

class CppFuzzyHandler : FuzzyHandler() {
    override val splitter = FuzzyMethodSplitter()
    override val parser = FuzzyCppParser()
}