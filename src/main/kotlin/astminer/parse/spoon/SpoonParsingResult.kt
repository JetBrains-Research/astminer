package astminer.parse.spoon

import astminer.common.model.*
import java.io.File

class SpoonParsingResult(file: File, inputDirectoryPath: String?) : ParsingResult<SpoonNode>(file, inputDirectoryPath) {
    override val root: SpoonNode = SpoonJavaParser().parseFile(file)
    override val splitter: TreeFunctionSplitter<SpoonNode> = SpoonJavaFunctionSplitter()
}

class SpoonParsingResultFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?): ParsingResult<out Node> =
        SpoonParsingResult(file, inputDirectoryPath)
}
