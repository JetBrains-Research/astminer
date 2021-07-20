package astminer.parse

import astminer.common.model.HandlerFactory
import astminer.config.FileExtension
import astminer.config.ParserType
import astminer.parse.antlr.AntlrJavaHandlerFactory
import astminer.parse.antlr.AntlrJavascriptHandlerFactory
import astminer.parse.antlr.AntlrPHPHandlerFactory
import astminer.parse.antlr.AntlrPythonHandlerFactory
import astminer.parse.gumtree.GumTreeJavaSrcmlHandlerFactory
import astminer.parse.gumtree.GumtreeJavaJDTHandlerFactory
import astminer.parse.gumtree.GumtreePythonHandlerFactory

fun getHandlerFactory(extension: FileExtension, parserType: ParserType): HandlerFactory {
    return when (parserType) {
        ParserType.GumTree -> getGumtreeHandlerFactory(extension)
        ParserType.Antlr -> getAntlrHandlerFactory(extension)
        ParserType.Fuzzy -> getFuzzyHandlerFactory(extension)
        ParserType.GumTreeSrcml -> getGumTreeSrcmlHandlerFactory(extension)
    }
}

private fun getGumtreeHandlerFactory(extension: FileExtension): HandlerFactory {
    return when (extension) {
        FileExtension.Java -> GumtreeJavaJDTHandlerFactory
        FileExtension.Python -> GumtreePythonHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getGumTreeSrcmlHandlerFactory(extension: FileExtension): HandlerFactory {
    return when (extension) {
        FileExtension.Java -> GumTreeJavaSrcmlHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getAntlrHandlerFactory(extension: FileExtension): HandlerFactory {
    return when (extension) {
        FileExtension.Java -> AntlrJavaHandlerFactory
        FileExtension.JavaScript -> AntlrJavascriptHandlerFactory
        FileExtension.Python -> AntlrPythonHandlerFactory
        FileExtension.PHP -> AntlrPHPHandlerFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getFuzzyHandlerFactory(extension: FileExtension): HandlerFactory {
    return when (extension) {
        FileExtension.C, FileExtension.Cpp -> FuzzyCppHandler
        else -> throw UnsupportedOperationException()
    }
}
