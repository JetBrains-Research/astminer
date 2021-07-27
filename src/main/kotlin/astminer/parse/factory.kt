package astminer.parse

import astminer.common.model.ParsingResultFactory
import astminer.config.FileExtension
import astminer.config.ParserType
import astminer.parse.antlr.AntlrJavaParsingResultFactory
import astminer.parse.antlr.AntlrJavascriptParsingResultFactory
import astminer.parse.antlr.AntlrPHPParsingResultFactory
import astminer.parse.antlr.AntlrPythonParsingResultFactory
import astminer.parse.fuzzy.FuzzyParsingResultFactory
import astminer.parse.gumtree.GumtreeJavaParsingResultFactory
import astminer.parse.gumtree.GumtreePythonParsingResultFactory

fun getParsingResultFactory(extension: FileExtension, parserType: ParserType): ParsingResultFactory {
    return when (parserType) {
        ParserType.GumTree -> getGumtreeParsingResultFactory(extension)
        ParserType.Antlr -> getAntlrParsingResultFactory(extension)
        ParserType.Fuzzy -> getFuzzyParsingResultFactory(extension)
    }
}

private fun getGumtreeParsingResultFactory(extension: FileExtension): ParsingResultFactory {
    return when (extension) {
        FileExtension.Java -> GumtreeJavaParsingResultFactory
        FileExtension.Python -> GumtreePythonParsingResultFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getAntlrParsingResultFactory(extension: FileExtension): ParsingResultFactory {
    return when (extension) {
        FileExtension.Java -> AntlrJavaParsingResultFactory
        FileExtension.JavaScript -> AntlrJavascriptParsingResultFactory
        FileExtension.Python -> AntlrPythonParsingResultFactory
        FileExtension.PHP -> AntlrPHPParsingResultFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getFuzzyParsingResultFactory(extension: FileExtension): ParsingResultFactory {
    return when (extension) {
        FileExtension.C, FileExtension.Cpp -> FuzzyParsingResultFactory
        else -> throw UnsupportedOperationException()
    }
}
