package astminer.parse

import astminer.common.model.ParsedFileFactory
import astminer.parse.antlr.AntlrJavaParsedFileFactory
import astminer.parse.antlr.AntlrJavascriptParsedFileFactory
import astminer.parse.antlr.AntlrPHPParsedFileFactory
import astminer.parse.antlr.AntlrPythonParsedFileFactory
import astminer.parse.gumtree.GumtreeJavaParsedFileFactory
import astminer.parse.gumtree.GumtreePythonParsedFileFactory

fun getParsedFileFactory(extension: String, parserType: String): ParsedFileFactory {
    return when (parserType) {
        "gumtree" -> getGumTreeParsedFileFactory(extension)
        "antlr" -> getAntlrParsedFileFactory(extension)
        "fuzzy" -> getFuzzyParsedFileFactory(extension)
        else -> throw UnsupportedParserOrLanguageException(extension, parserType)
    }
}

private fun getGumTreeParsedFileFactory(extension: String): ParsedFileFactory {
    return when (extension) {
        "java" -> GumtreeJavaParsedFileFactory
        "python" -> GumtreePythonParsedFileFactory
        else -> throw UnsupportedParserOrLanguageException(extension, "GumTree")
    }
}

private fun getAntlrParsedFileFactory(extension: String): ParsedFileFactory {
    return when (extension) {
        "java" -> AntlrJavaParsedFileFactory
        "javascript" -> AntlrJavascriptParsedFileFactory
        "python" -> AntlrPythonParsedFileFactory
        "php" -> AntlrPHPParsedFileFactory
        else -> throw UnsupportedParserOrLanguageException(extension, "ANTLR")
    }
}

private fun getFuzzyParsedFileFactory(extension: String): ParsedFileFactory {
    return when (extension) {
        "c", "cpp" -> FuzzyCppParsedFile
        else -> throw UnsupportedParserOrLanguageException(extension, "Fuzzy parser")
    }
}

class UnsupportedParserOrLanguageException(extension: String, parser: String) : Exception() {
    override val message: String =
        "At the moment, astminer does not support parsing files with the $extension extension by the parser $parser. " +
                "If you think this is a bug or you want this parser or language to be supported write to the issue section."
}
