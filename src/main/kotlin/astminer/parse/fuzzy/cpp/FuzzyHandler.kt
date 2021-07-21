package astminer.parse.fuzzy.cpp

import astminer.common.model.HandlerFactory
import astminer.common.model.LanguageHandler
import astminer.common.model.ParseResult
import java.io.File

object FuzzyHandler : HandlerFactory {
    override fun createHandler(file: File): LanguageHandler<FuzzyNode> = CppFuzzyHandler(file)

    class CppFuzzyHandler(file: File) : LanguageHandler<FuzzyNode>() {
        override val splitter = FuzzyFunctionSplitter()
        override val parseResult: ParseResult<FuzzyNode> = FuzzyCppParser().parseFile(file)
    }
}
