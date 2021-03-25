package astminer.parse

fun getLanguageHandler(extension: String, parserType: String): LanguageHandler {
    return when (parserType) {
        "gumtree" -> getGumtreeHandler(extension)
        "antlr" -> getAntlrHandler(extension)
        else -> throw UnsupportedOperationException()
    }
}

private fun getGumtreeHandler(extension: String): GumTreeHandler {
    return when (extension) {
        "java" -> JavaGumtreeHandler()
        "python" -> PythonGumTreeHandler()
        else -> throw UnsupportedOperationException()
    }
}

private fun getAntlrHandler(extension: String): AntlrLanguageHandler {
    return when (extension) {
        "java" -> AntlrJavaHandler()
        "javascript" -> AntlrJavascriptHandler()
        "python" -> AntlrPythonHandler()
        else -> throw UnsupportedOperationException()
    }
}
