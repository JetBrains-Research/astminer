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
        else -> throw UnsupportedOperationException()
    }
}

private fun getGumTreeParsedFileFactory(extension: String): ParsedFileFactory {
    return when (extension) {
        "java" -> GumtreeJavaParsedFileFactory
        "python" -> GumtreePythonParsedFileFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getAntlrParsedFileFactory(extension: String): ParsedFileFactory {
    return when (extension) {
        "java" -> AntlrJavaParsedFileFactory
        "javascript" -> AntlrJavascriptParsedFileFactory
        "python" -> AntlrPythonParsedFileFactory
        "php" -> AntlrPHPParsedFileFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getFuzzyParsedFileFactory(extension: String): ParsedFileFactory {
    return when (extension) {
        "c", "cpp" -> FuzzyCppParsedFile
        else -> throw UnsupportedOperationException()
    }
}
