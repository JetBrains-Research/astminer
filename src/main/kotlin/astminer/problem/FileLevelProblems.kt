package astminer.problem

import astminer.common.model.Node
import astminer.common.model.ParseResult
import java.io.File

interface FileLevelProblem : Problem {
    override val granularity: Granularity
        get() = Granularity.File

    fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node>?
}

/**
 * Labels files with folder names
 */
object FileNameExtractor : FileLevelProblem {
    override fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node> =
        parseResult.labeledWith(File(parseResult.filePath).name)

}

/**
 * Labels files with folder names
 */
object FolderNameExtractor : FileLevelProblem {
    override fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node>? {
        val folderName = File(parseResult.filePath).parentFile?.name ?: return null
        return parseResult.labeledWith(folderName)
    }
}
