package astminer.parse.javaparser

import astminer.common.model.*
import java.io.File

class JavaParserParsingResult(file: File) : ParsingResult<JavaParserNode>(file) {
    override val root: JavaParserNode = JavaParserParseWrapper().parseFile(file)
    override val splitter: TreeFunctionSplitter<JavaParserNode> = JavaparserMethodSplitter()
}

object JavaParserParsedFileFactory : ParsingResultFactory {
    override fun parse(file: File): ParsingResult<out Node> = JavaParserParsingResult(file)
}
