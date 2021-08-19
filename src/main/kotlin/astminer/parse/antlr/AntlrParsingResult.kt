package astminer.parse.antlr

import astminer.common.model.*
import astminer.parse.antlr.java.JavaFunctionSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.javascript.JavaScriptFunctionSplitter
import astminer.parse.antlr.javascript.JavaScriptParser
import astminer.parse.antlr.php.PHPFunctionSplitter
import astminer.parse.antlr.php.PHPParser
import astminer.parse.antlr.python.PythonFunctionSplitter
import astminer.parse.antlr.python.PythonParser
import java.io.File

object AntlrJavaParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?) =
        AntlrJavaParsingResult(file, inputDirectoryPath)

    class AntlrJavaParsingResult(file: File, inputDirectoryPath: String?) :
        ParsingResult<AntlrNode>(file, inputDirectoryPath) {
        override val root = JavaParser().parseFile(file)
        override val splitter = JavaFunctionSplitter()
    }
}

object AntlrPythonParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?) =
        AntlrPythonParsingResult(file, inputDirectoryPath)

    class AntlrPythonParsingResult(file: File, inputDirectoryPath: String?) :
        ParsingResult<AntlrNode>(file, inputDirectoryPath) {
        override val root = PythonParser().parseFile(file)
        override val splitter = PythonFunctionSplitter()
    }
}

object AntlrJavascriptParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?) =
        AntlrJavascriptParsingResult(file, inputDirectoryPath)

    class AntlrJavascriptParsingResult(file: File, inputDirectoryPath: String?) :
        ParsingResult<AntlrNode>(file, inputDirectoryPath) {
        override val root = JavaScriptParser().parseFile(file)
        override val splitter = JavaScriptFunctionSplitter()
    }
}

object AntlrPHPParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?) =
        AntlrPHPParsingResult(file, inputDirectoryPath)

    class AntlrPHPParsingResult(file: File, inputDirectoryPath: String?) :
        ParsingResult<AntlrNode>(file, inputDirectoryPath) {
        override val root = PHPParser().parseFile(file)
        override val splitter = PHPFunctionSplitter()
    }
}
