package astminer.parse

import astminer.common.model.ParseResult
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parse.fuzzy.cpp.FuzzyMethodSplitter
import astminer.parse.fuzzy.cpp.FuzzyNode
import java.io.File

object CppFuzzyHandlerFactory: HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<FuzzyNode> = CppFuzzyHandler(file)
}

class CppFuzzyHandler(file: File) : LanguageHandler<FuzzyNode>() {
    override val splitter = FuzzyMethodSplitter()
    override val parseResult: ParseResult<FuzzyNode> = FuzzyCppParser().parseFile(file)
}