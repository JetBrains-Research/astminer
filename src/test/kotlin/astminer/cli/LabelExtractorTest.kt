package astminer.cli

import astminer.common.model.*
import astminer.parse.antlr.AntlrNode
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

internal class LabelExtractorTest {

    companion object {
        private const val PATH_STRING = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private const val FILENAME = "file.txt"
        private const val METHOD_NAME = "method"
    }

    private var dummyRoot = AntlrNode("", null, null)

    private fun makeFunctionInfo(nameNode: AntlrNode) = object : FunctionInfo<AntlrNode> {
        override val root: AntlrNode = dummyRoot
        override val nameNode: AntlrNode = nameNode
    }

    @Before
    fun setUp() {
        dummyRoot = AntlrNode("", null, null)
    }

    @Test
    fun testNonEmptyFilePathExtractor() {
        val labelExtractor = FilePathExtractor()
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(nonEmptyParseResult)
        assertEquals(1, labeledParseResults.size)
        val (root, label) = labeledParseResults[0]
        assertEquals(dummyRoot, root)
        assertEquals(PATH_STRING, label)
    }

    @Test
    fun testNonEmptyFolderExtractor() {
        val labelExtractor = FolderExtractor()
        val nonEmptyParseResult = ParseResult(dummyRoot, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(nonEmptyParseResult)
        assertEquals(1, labeledParseResults.size)
        val (root, label) = labeledParseResults[0]
        assertEquals(dummyRoot, root)
        assertEquals(FOLDER, label)
    }

    @Test
    fun `test method name extractor extracts correct method name`() {
        val nameNode = AntlrNode("", dummyRoot, METHOD_NAME)
        val functionInfo = makeFunctionInfo(nameNode)
        val methodNameExtractor = MethodNameExtractor()
        val label = methodNameExtractor.extractLabel(functionInfo, PATH_STRING)
        assertEquals(METHOD_NAME, label)
    }

    @Test
    fun `test method name extractor hides method name with technical token`() {
        val nameNode = AntlrNode("", dummyRoot, METHOD_NAME)
        val functionInfo = makeFunctionInfo(nameNode)
        val methodNameExtractor = MethodNameExtractor()
        methodNameExtractor.extractLabel(functionInfo, PATH_STRING)
        assertEquals("METHOD_NAME", nameNode.technicalToken)
    }
}
