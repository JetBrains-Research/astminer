package astminer.cli

import astminer.common.model.ParseResult
import astminer.parse.antlr.SimpleNode
import org.junit.Test
import kotlin.test.assertEquals

internal class LabelExtractorTest {

    companion object {
        private const val PATH_STRING = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private const val FILENAME = "file.txt"
    }

    @Test
    fun testEmptyFilePathExtractor() {
        val labelExtractor = FilePathExtractor()
        val emptyParseResult = ParseResult(null, PATH_STRING)
        assertEquals(PATH_STRING, labelExtractor.extractLabel(emptyParseResult))
    }

    @Test
    fun testNonEmptyFilePathExtractor() {
        val labelExtractor = FilePathExtractor()
        val nonEmptyParseResult = ParseResult(SimpleNode("", null, null), PATH_STRING)
        assertEquals(PATH_STRING, labelExtractor.extractLabel(nonEmptyParseResult))
    }

    @Test
    fun testCode2VecFolderExtractor() {
        val parseResult = ParseResult(null, PATH_STRING)

        val fileLabelExtractor = Code2VecLabelExtractor("file", true)
        assertEquals(FOLDER, fileLabelExtractor.extractLabel(parseResult))

        val pathLabelExtractor = Code2VecLabelExtractor("file", false)
        assertEquals(FILENAME, pathLabelExtractor.extractLabel(parseResult))

        val wrongGranularityLabelExtractor = Code2VecLabelExtractor("notFile", true)
        assertEquals(FILENAME, wrongGranularityLabelExtractor.extractLabel(parseResult))
    }
}
