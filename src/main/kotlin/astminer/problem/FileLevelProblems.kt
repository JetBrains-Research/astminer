package astminer.problem

import astminer.common.model.Node
import astminer.common.model.ParseResult
import java.io.File

interface FileLevelProblem {
    fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node>?
}

/**
 * Labels files with folder names
 */
object FilePathExtractor : FileLevelProblem {
    override fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node> = parseResult.labeledWithFilePath()
}

/**
 * Labels files with folder names
 */
object FolderExtractor : FileLevelProblem {
    override fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node>? {
        val folderName = File(parseResult.filePath).parentFile?.name ?: return null
        return parseResult.labeledWith(folderName)
    }
}
