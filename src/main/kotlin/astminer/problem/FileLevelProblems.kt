package astminer.problem

import astminer.common.model.Node
import astminer.common.model.ParseResult
import java.io.File

interface FileLevelProblem {
    fun process(parseResult: ParseResult<Node>): LabeledResult<Node>?
}

object FilePathExtractor : FileLevelProblem {
    override fun process(parseResult: ParseResult<Node>): LabeledResult<Node> = parseResult.labeledWithFilePath()
}

class FolderExtractor : FileLevelProblem {
    override fun process(parseResult: ParseResult<Node>): LabeledResult<Node>? {
        val folderName = File(parseResult.filePath).parentFile.name ?: return null
        return parseResult.labeledWith(folderName)
    }
}
