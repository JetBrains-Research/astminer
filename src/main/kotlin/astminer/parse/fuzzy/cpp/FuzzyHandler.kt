package astminer.parse

import astminer.common.model.HandlerFactory
import astminer.common.model.LanguageHandler
import astminer.common.model.ParseResult
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parse.fuzzy.cpp.FuzzyFunctionSplitter
import astminer.parse.fuzzy.cpp.FuzzyNode
import java.io.File

object FuzzyCppHandler : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<FuzzyNode> = CppFuzzyHandler(file)


    class CppFuzzyHandler(file: File) : LanguageHandler<FuzzyNode>() {
        override val splitter = FuzzyFunctionSplitter()
        override val parseResult: ParseResult<FuzzyNode> = FuzzyCppParser().parseFile(file)
    }
}