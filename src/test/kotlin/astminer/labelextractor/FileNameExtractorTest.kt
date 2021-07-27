package astminer.labelextractor

import astminer.common.DummyNode
import astminer.common.DummyParsingResult
import astminer.common.model.LabeledResult
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class FileNameExtractorTest {
    @Test
    fun `test file path extractor returns the same root and file path and labels with file path`() {
        val nonEmptyParseResult = DummyParsingResult(File(PATH), dummyRoot)
        val labeledParseResult = FileNameExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, FILE_NAME, PATH), labeledParseResult)
    }

    companion object {
        private const val FILE_NAME = "file.txt"
        private const val PATH = "random/folder/$FILE_NAME"
        private var dummyRoot = DummyNode("")
    }
}
