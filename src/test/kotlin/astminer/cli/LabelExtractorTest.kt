package astminer.cli

import astminer.common.getNormalizedToken
import astminer.common.model.ElementNode
import astminer.common.model.MethodInfo
import astminer.common.model.MethodNode
import astminer.common.model.ParseResult
import astminer.parse.antlr.SimpleNode
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LabelExtractorTest {

    companion object {
        private const val PATH_STRING = "random/folder/file.txt"
        private const val FOLDER = "folder"
        private const val FILENAME = "file.txt"
        private const val METHOD_NAME = "method"
        private val DUMMY_ROOT = SimpleNode("", null, null)
    }

    @Test
    fun testEmptyFilePathExtractor() {
        val labelExtractor = FilePathExtractor(false)
        val emptyParseResult = ParseResult(null, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(emptyParseResult)
        assertTrue { labeledParseResults.isEmpty() }
    }

    @Test
    fun testNonEmptyFilePathExtractor() {
        val labelExtractor = FilePathExtractor(false)
        val nonEmptyParseResult = ParseResult(DUMMY_ROOT, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(nonEmptyParseResult)
        assertEquals(1, labeledParseResults.size)
        val (root, label) = labeledParseResults[0]
        assertEquals(DUMMY_ROOT, root)
        assertEquals(PATH_STRING, label)
    }

    @Test
    fun testEmptyFolderExtractor() {
        val labelExtractor = FolderExtractor(false)
        val emptyParseResult = ParseResult(null, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(emptyParseResult)
        assertTrue { labeledParseResults.isEmpty() }
    }

    @Test
    fun testNonEmptyFolderExtractor() {
        val labelExtractor = FolderExtractor(false)
        val nonEmptyParseResult = ParseResult(DUMMY_ROOT, PATH_STRING)
        val labeledParseResults = labelExtractor.toLabeledData(nonEmptyParseResult)
        assertEquals(1, labeledParseResults.size)
        val (root, label) = labeledParseResults[0]
        assertEquals(DUMMY_ROOT, root)
        assertEquals(FOLDER, label)
    }

    @Test
    fun testMethodNameExtractor() {
        val nameNode = SimpleNode("", DUMMY_ROOT, METHOD_NAME)
        val methodInfo = MethodInfo<SimpleNode>(
                MethodNode(DUMMY_ROOT, null, nameNode),
                ElementNode(null, null),
                emptyList()
        )
        processNodeToken(nameNode, false)
        val methodNameExtractor = MethodNameExtractor(false, hideMethodNames = false)
        val label = methodNameExtractor.extractLabel(methodInfo, PATH_STRING)
        assertEquals(METHOD_NAME, label)
        assertEquals(METHOD_NAME, nameNode.getNormalizedToken())
    }

    @Test
    fun testMethodNameExtractorHide() {
        val nameNode = SimpleNode("", DUMMY_ROOT, METHOD_NAME)
        val methodInfo = MethodInfo<SimpleNode>(
                MethodNode(DUMMY_ROOT, null, nameNode),
                ElementNode(null, null),
                emptyList()
        )
        processNodeToken(nameNode, false)
        val methodNameExtractor = MethodNameExtractor(false, hideMethodNames = true)
        val label = methodNameExtractor.extractLabel(methodInfo, PATH_STRING)
        assertEquals(METHOD_NAME, label)
        assertEquals("METHOD_NAME", nameNode.getNormalizedToken())
    }
}
