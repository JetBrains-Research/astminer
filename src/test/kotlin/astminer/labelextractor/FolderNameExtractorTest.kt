package astminer.labelextractor

import astminer.common.DummyNode
import astminer.common.DummyParsingResult
import astminer.common.model.LabeledResult
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FolderNameExtractorTest {

    @Test
    fun `test folder extractor returns null when folder is empty or not found`() {
        val nonEmptyParseResult = DummyParsingResult(File(""), dummyRoot)
        val labeledParseResult = FolderNameExtractor.process(nonEmptyParseResult)

        assertNull(labeledParseResult)
    }

    @Test
    fun `test folder extractor extracts folder when it is not empty`() {
        val nonEmptyParseResult = DummyParsingResult(File(PATH), dummyRoot)
        val labeledParseResult = FolderNameExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, FOLDER, PATH), labeledParseResult)
    }

    companion object {
        private const val PATH = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private var dummyRoot = DummyNode("")
    }
}
