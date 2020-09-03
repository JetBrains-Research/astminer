package astminer.cli

import astminer.common.model.Node
import astminer.common.model.ParseResult
import java.io.File

interface LabelExtractor {
    fun extractLabel(parseResult: ParseResult<out Node>): String
}

class FilePathExtractor : LabelExtractor {
    override fun extractLabel(parseResult: ParseResult<out Node>) = parseResult.filePath
}

class Code2VecLabelExtractor(private val granularityLevel: String, private val folderLabel: Boolean) : LabelExtractor {
    override fun extractLabel(parseResult: ParseResult<out Node>): String {
        val fullPath = File(parseResult.filePath)
        val (parentName, fileName) = arrayOf(fullPath.parentFile.name, fullPath.name)
        return if (granularityLevel == "file" && folderLabel) parentName else fileName
    }
}
