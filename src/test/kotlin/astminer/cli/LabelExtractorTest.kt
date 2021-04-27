package astminer.cli

import astminer.common.getTechnicalToken
import astminer.common.model.ElementNode
import astminer.common.model.MethodInfo
import astminer.common.model.MethodNode
import astminer.common.model.ParseResult
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LabelExtractorTest {

    companion object {
        private const val PATH_STRING = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private const val FILENAME = "file.txt"
        private const val METHOD_NAME = "method"
        private val DUMMY_ROOT = AntlrNode("", null, null)
    }

    @Test
    fun testNonEmptyFilePathExtractor() {
        val labelExtractor = FilePathExtractor()
        val nonEmptyParseResult = ParseResult(DUMMY_ROOT, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(nonEmptyParseResult)
        assertEquals(1, labeledParseResults.size)
        val (root, label) = labeledParseResults[0]
        assertEquals(DUMMY_ROOT, root)
        assertEquals(PATH_STRING, label)
    }

    @Test
    fun testNonEmptyFolderExtractor() {
        val labelExtractor = FolderExtractor()
        val nonEmptyParseResult = ParseResult(DUMMY_ROOT, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(nonEmptyParseResult)
        assertEquals(1, labeledParseResults.size)
        val (root, label) = labeledParseResults[0]
        assertEquals(DUMMY_ROOT, root)
        assertEquals(FOLDER, label)
    }

    @Test
    fun testMethodNameExtractor() {
        val nameNode = AntlrNode("", DUMMY_ROOT, METHOD_NAME)
        val methodInfo = MethodInfo<AntlrNode>(
                MethodNode(DUMMY_ROOT, null, nameNode),
                ElementNode(null, null),
                emptyList()
        )
        val methodNameExtractor = MethodNameExtractor(false)
        val label = methodNameExtractor.extractLabel(methodInfo, PATH_STRING)
        assertEquals(METHOD_NAME, label)
        assertNull(nameNode.getTechnicalToken())
    }

    @Test
    fun testMethodNameExtractorHide() {
        val nameNode = AntlrNode("", DUMMY_ROOT, METHOD_NAME)
        val methodInfo = MethodInfo<AntlrNode>(
                MethodNode(DUMMY_ROOT, null, nameNode),
                ElementNode(null, null),
                emptyList()
        )
        val methodNameExtractor = MethodNameExtractor(true)
        val label = methodNameExtractor.extractLabel(methodInfo, PATH_STRING)
        assertEquals(METHOD_NAME, label)
        assertEquals("METHOD_NAME", nameNode.getTechnicalToken())
    }
}
