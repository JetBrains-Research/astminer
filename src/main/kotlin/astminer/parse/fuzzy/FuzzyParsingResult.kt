package astminer.parse.fuzzy

import astminer.common.model.ParsingResult
import astminer.common.model.ParsingResultFactory
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parse.fuzzy.cpp.FuzzyFunctionSplitter
import java.io.File

object FuzzyParsingResult : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<FuzzyNode> = CppFuzzyParsingResult(file)

    class CppFuzzyParsingResult(file: File) : ParsingResult<FuzzyNode>(file) {
        override val root = FuzzyCppParser().parseFile(file)
        override val splitter = FuzzyFunctionSplitter()
    }
}
