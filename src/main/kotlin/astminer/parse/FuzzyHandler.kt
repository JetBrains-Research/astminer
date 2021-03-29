package astminer.parse

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.common.model.TreeMethodSplitter
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.cpp.FuzzyNode
import java.io.File

object CppFuzzyHandlerFactory: HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<FuzzyNode> = CppFuzzyHandler(file)
}

class CppFuzzyHandler(file: File) : LanguageHandler<FuzzyNode>() {
    override val splitter = FuzzyMethodSplitter()
    override val parseResult: ParseResult<FuzzyNode> = FuzzyCppParser().parseFile(file)
}