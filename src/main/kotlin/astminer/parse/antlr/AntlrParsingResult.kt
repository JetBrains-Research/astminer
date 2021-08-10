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
    override fun parse(file: File) = AntlrJavaParsingResult(file)

    class AntlrJavaParsingResult(file: File) : ParsingResult<AntlrNode>(file) {
        override val root = JavaParser().parseFile(file)
        override val splitter = JavaFunctionSplitter()
    }
}

object AntlrPythonParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File) = AntlrPythonParsingResult(file)

    class AntlrPythonParsingResult(file: File) : ParsingResult<AntlrNode>(file) {
        override val root = PythonParser().parseFile(file)
        override val splitter = PythonFunctionSplitter()
    }
}

object AntlrJavascriptParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File) = AntlrJavascriptParsingResult(file)

    class AntlrJavascriptParsingResult(file: File) : ParsingResult<AntlrNode>(file) {
        override val root = JavaScriptParser().parseFile(file)
        override val splitter = JavaScriptFunctionSplitter()
    }
}

object AntlrPHPParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<out Node> = AntlrPHPParsingResult(file)

    class AntlrPHPParsingResult(file: File) : ParsingResult<AntlrNode>(file) {
        override val root = PHPParser().parseFile(file)
        override val splitter = PHPFunctionSplitter()
    }
}
