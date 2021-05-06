package astminer.problem

import astminer.common.model.ParseResult
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FolderNameExtractorTest {
    companion object {
        private const val PATH = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private var dummyRoot = AntlrNode("", null, null)
    }

    @Test
    fun `test folder extractor returns null when folder is empty or not found`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, "")
        val labeledParseResult = FolderExtractor.process(nonEmptyParseResult)

        assertNull(labeledParseResult)
    }

    @Test
    fun `test folder extractor extracts folder when it is not empty`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH)
        val labeledParseResult = FolderExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, FOLDER, PATH), labeledParseResult)
    }
}
