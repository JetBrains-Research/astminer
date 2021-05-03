package astminer.problem

import astminer.common.getTechnicalToken
import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class ProblemTest {

    companion object {
        private const val PATH_STRING = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private const val FILENAME = "file.txt"
        private const val METHOD_NAME = "method"
    }

    private var dummyRoot = AntlrNode("", null, null)

    @Before
    fun setUp() {
        dummyRoot = AntlrNode("", null, null)
    }

    @Test
    fun `test file path extractor returns the same root and file path`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH_STRING)
        val labeledParseResult = FilePathExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, PATH_STRING, PATH_STRING), labeledParseResult)
    }

    @Test
    fun `test folder extractor returns null when folder is empty`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, "")
        val labeledParseResult = FolderExtractor.process(nonEmptyParseResult)

        assertNull(labeledParseResult)
    }

    @Test
    fun `test folder extractor extracts folder when it is not empty`() {
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH_STRING)
        val labeledParseResult = FolderExtractor.process(nonEmptyParseResult)

        assertEquals(LabeledResult(dummyRoot, FOLDER, PATH_STRING), labeledParseResult)
    }

    @Test
    fun `test method name extractor extracts correct method name`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val nameNode = AntlrNode("", dummyRoot, METHOD_NAME)
            override val filePath = PATH_STRING
            override val root = dummyRoot
        }
        val labeledResult = FunctionNameProblem.process(functionInfo)

        assertEquals(LabeledResult(dummyRoot, METHOD_NAME, PATH_STRING), labeledResult)
    }

    @Test
    fun `test method name extractor hides method name with technical token`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val nameNode = AntlrNode("", dummyRoot, METHOD_NAME)
            override val filePath = PATH_STRING
            override val root = dummyRoot
        }
        FunctionNameProblem.process(functionInfo)
        assertEquals("METHOD_NAME", functionInfo.nameNode.getTechnicalToken())
    }
}
