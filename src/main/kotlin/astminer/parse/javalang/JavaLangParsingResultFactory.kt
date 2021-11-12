package astminer.parse.javalang

import astminer.common.SimpleNode
import astminer.common.model.Node
import astminer.common.model.ParsingResult
import astminer.common.model.ParsingResultFactory
import astminer.common.model.TreeFunctionSplitter
import java.io.File

object JavaLangParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?): ParsingResult<out Node> =
        JavaLangParsingResult(file, inputDirectoryPath)

    class JavaLangParsingResult(file: File, inputDir: String?) : ParsingResult<SimpleNode>(file, inputDir) {
        override val root: SimpleNode = JavaLangParser().parseFile(file)
        override val splitter: TreeFunctionSplitter<SimpleNode> = JavaLangFunctionSplitter()
    }
}
