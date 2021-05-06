package astminer.problem

import astminer.common.model.Node
import astminer.common.model.ParseResult
import java.io.File

interface FileLevelProblem : Problem<ParseResult<out Node>>

/**
 * Labels files with folder names
 */
object FilePathExtractor : FileLevelProblem {
    override fun process(entity: ParseResult<out Node>): LabeledResult<out Node> = entity.labeledWithFilePath()
}

/**
 * Labels files with folder names
 */
object FolderExtractor : FileLevelProblem {
    override fun process(entity: ParseResult<out Node>): LabeledResult<out Node>? {
        val folderName = File(entity.filePath).parentFile?.name ?: return null
        return entity.labeledWith(folderName)
    }
}
