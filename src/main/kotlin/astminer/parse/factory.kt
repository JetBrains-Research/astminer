package astminer.parse

import java.io.File

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
        "java" -> JavaGumtreeHandlerFactory
        "python" -> PythonGumTreeHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getAntlrHandlerFactory(extension: String): HandlerFactory {
    return when (extension) {
        "java" -> AntlrJavaHandlerFactory
        "javascript" -> AntlrJavascriptHandlerFactory
        "python" -> AntlrPythonHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getFuzzyHandlerFactory(extension: String): HandlerFactory {
    return when (extension) {
        "c", "cpp" -> CppFuzzyHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}
