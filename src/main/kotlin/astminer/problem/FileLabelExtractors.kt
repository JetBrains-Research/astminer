package astminer.problem

import astminer.common.model.Node
import astminer.common.model.ParseResult
import java.io.File

interface FileLabelExtractor : LabelExtractor {
    override val granularity: Granularity
        get() = Granularity.File

    fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node>?
}

/**
 * Labels files with folder names
 */
object FileNameExtractor : FileLabelExtractor {
    override fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node> =
        parseResult.labeledWith(File(parseResult.filePath).name)
}

/**
 * Labels files with folder names
 */
object FolderNameExtractor : FileLabelExtractor {
    override fun process(parseResult: ParseResult<out Node>): LabeledResult<out Node>? {
        val folderName = File(parseResult.filePath).parentFile?.name ?: return null
        return parseResult.labeledWith(folderName)
    }
}
