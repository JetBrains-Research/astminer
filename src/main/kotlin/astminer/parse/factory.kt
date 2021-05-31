package astminer.parse

import astminer.common.model.HandlerFactory
import astminer.parse.antlr.AntlrJavaHandlerFactory
import astminer.parse.antlr.AntlrJavascriptHandlerFactory
import astminer.parse.antlr.AntlrPHPHandlerFactory
import astminer.parse.antlr.AntlrPythonHandlerFactory
import astminer.parse.gumtree.GumtreeJavaHandlerFactory
import astminer.parse.gumtree.GumtreePythonHandlerFactory

fun getHandlerFactory(extension: String, parserType: String): HandlerFactory {
    return when (parserType) {
        "gumtree" -> getGumtreeHandlerFactory(extension)
        "antlr" -> getAntlrHandlerFactory(extension)
        "fuzzy" -> getFuzzyHandlerFactory(extension)
        else -> throw UnsupportedOperationException()
    }
}

private fun getGumtreeHandlerFactory(extension: String): HandlerFactory {
    return when (extension) {
        "java" -> GumtreeJavaHandlerFactory
        "python" -> GumtreePythonHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getAntlrHandlerFactory(extension: String): HandlerFactory {
    return when (extension) {
        "java" -> AntlrJavaHandlerFactory
        "javascript" -> AntlrJavascriptHandlerFactory
        "python" -> AntlrPythonHandlerFactory
        "php" -> AntlrPHPHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getFuzzyHandlerFactory(extension: String): HandlerFactory {
    return when (extension) {
        "c", "cpp" -> FuzzyCppHandler
        else -> throw UnsupportedOperationException()
    }
}
