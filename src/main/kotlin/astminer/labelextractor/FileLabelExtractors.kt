package astminer.labelextractor

import astminer.common.model.*

/**
 * Labels files with folder names
 */
object FileNameExtractor : FileLabelExtractor {
    override fun process(parseResult: ParsingResult<out Node>): LabeledResult<out Node> =
        parseResult.labeledWith(parseResult.file.name)
}

/**
 * Labels files with folder names
 */
object FolderNameExtractor : FileLabelExtractor {
    override fun process(parseResult: ParsingResult<out Node>): LabeledResult<out Node>? {
        val folderName = parseResult.file.parentFile?.name ?: return null
        return parseResult.labeledWith(folderName)
    }
}
