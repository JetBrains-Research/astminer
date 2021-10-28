package astminer.parse.treesitter.java

import astminer.common.SimpleNode
import astminer.common.model.ParsingResult
import astminer.common.model.ParsingResultFactory
import astminer.common.model.TreeFunctionSplitter
import java.io.File

class TreeSitterJavaFactory : ParsingResultFactory {
    override fun parse(file: File, inputDirectoryPath: String?) = TreeSitterJavaParsingResult(file, inputDirectoryPath)

    class TreeSitterJavaParsingResult(file: File, inputDirectoryPath: String?) : ParsingResult<SimpleNode>(
        file,
        inputDirectoryPath
    ) {
        override val root: SimpleNode = TreeSitterJavaParser().parseFile(file)
        override val splitter: TreeFunctionSplitter<SimpleNode> = TreeSitterJavaFunctionSplitter()
    }
}
