package astminer.problem

import astminer.common.model.ParseResult
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals

class FilePathExtractorTest {
    companion object {
        private const val PATH = "random/folder/file.txt"
        private var dummyRoot = AntlrNode("", null, null)
    }

    @Test
    fun `test file path extractor returns the same root and file path and labels with file path`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH)
        val labeledParseResult = FilePathExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, PATH, PATH), labeledParseResult)
    }
}