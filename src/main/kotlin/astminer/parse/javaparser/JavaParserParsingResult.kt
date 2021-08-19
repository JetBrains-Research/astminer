package astminer.parse.javaparser

import astminer.common.model.*
import java.io.File

class JavaParserParsingResult(file: File, inputDirectoryPath: String?) :
    ParsingResult<JavaParserNode>(file, inputDirectoryPath) {
    override val root: JavaParserNode = JavaParserParseWrapper().parseFile(file)
    override val splitter: TreeFunctionSplitter<JavaParserNode> = JavaparserMethodSplitter()
}

object JavaParserParsedFileFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?): ParsingResult<out Node> =
        JavaParserParsingResult(file, inputDirectoryPath)
}
