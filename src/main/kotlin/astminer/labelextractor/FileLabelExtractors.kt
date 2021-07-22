package astminer.labelextractor

import astminer.common.model.*
import java.io.File

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
