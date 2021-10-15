package astminer.parse

import astminer.common.model.ParsingResultFactory
import astminer.config.FileExtension
import astminer.config.ParserType
import astminer.parse.antlr.AntlrJavaParsingResultFactory
import astminer.parse.antlr.AntlrJavascriptParsingResultFactory
import astminer.parse.antlr.AntlrPHPParsingResultFactory
import astminer.parse.antlr.AntlrPythonParsingResultFactory
import astminer.parse.fuzzy.FuzzyParsingResultFactory
import astminer.parse.gumtree.GumtreeJavaJDTParsingResultFactory
import astminer.parse.gumtree.GumtreeJavaSrcmlParsingResultFactory
import astminer.parse.gumtree.GumtreePythonParsingResultFactory
import astminer.parse.javalang.JavaLangParsingResultFactory
import astminer.parse.javaparser.JavaParserParsedFileFactory
import astminer.parse.spoon.SpoonParsingResultFactory
import astminer.parse.treesitter.java.TreeSitterJavaFactory

fun getParsingResultFactory(extension: FileExtension, parserType: ParserType): ParsingResultFactory {
    return when (parserType) {
        ParserType.GumTree -> getGumtreeParsingResultFactory(extension)
        ParserType.Antlr -> getAntlrParsingResultFactory(extension)
        ParserType.Fuzzy -> getFuzzyParsingResultFactory(extension)
        ParserType.JavaParser -> getJavaParserParsingFactory(extension)
        ParserType.GumTreeSrcml -> getGumTreeSrcmlFactory(extension)
        ParserType.Spoon -> getSpoonParsingResultFactory(extension)
        ParserType.JavaLang -> getJavaLangFactory(extension)
        ParserType.TreeSitter -> getTreeSitterFactory(extension)
    }
}

private fun getGumtreeParsingResultFactory(extension: FileExtension): ParsingResultFactory {
    return when (extension) {
        FileExtension.Java -> GumtreeJavaJDTParsingResultFactory
        FileExtension.Python -> GumtreePythonParsingResultFactory
        else -> throw UnsupportedOperationException()
    }
}

private fun getGumTreeSrcmlFactory(extension: FileExtension): ParsingResultFactory {
    if (extension == FileExtension.Java) {
        return GumtreeJavaSrcmlParsingResultFactory
    } else {
        throw UnsupportedOperationException()
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

private fun getJavaParserParsingFactory(extension: FileExtension): ParsingResultFactory {
    if (extension == FileExtension.Java) {
        return JavaParserParsedFileFactory
    } else {
        throw UnsupportedOperationException()
    }
}

private fun getSpoonParsingResultFactory(extension: FileExtension): ParsingResultFactory {
    if (extension == FileExtension.Java) {
        return SpoonParsingResultFactory()
    } else {
        throw UnsupportedOperationException()
    }
}

private fun getJavaLangFactory(extension: FileExtension): ParsingResultFactory {
    if (extension == FileExtension.Java) {
        return JavaLangParsingResultFactory
    } else {
        throw UnsupportedOperationException()
    }
}

private fun getTreeSitterFactory(extension: FileExtension): ParsingResultFactory {
    if (extension == FileExtension.Java) {
        return TreeSitterJavaFactory()
    } else {
        throw UnsupportedOperationException()
    }
}
