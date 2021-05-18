package astminer.parse.gumtree.python

import astminer.checkExecutable
import astminer.parse.ParsingException
import org.junit.After
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GumTreePythonParserTest {
    private val parser: GumTreePythonParser = GumTreePythonParser()
    private val testFolder = File(".python_parser_test_tmp")
    private val testFile = testFolder.resolve("test_file.py")

    @Before
    fun mkdir() {
        Assume.assumeTrue(checkExecutable("pythonparser"))
        testFolder.mkdirs()
        testFile.createNewFile()
    }

    @After
    fun rmdir() {
        testFolder.deleteRecursively()
    }

    @Test(expected = Test.None::class)
    fun emptyFile() {
        val node = parser.parseInputStream(testFile.inputStream())
        assertNotNull(node)
        assertTrue(node.wrappedNode.children.isEmpty())
    }

    @Test(expected = ParsingException::class)
    fun invalidCode() {
        testFile.writeText("INVALID PYTHON CODE")
        parser.parseInputStream(testFile.inputStream())
    }

    @Test(expected = Test.None::class)
    fun goodFile() {
        val node = parser.parseInputStream(
            File("src/test/resources/gumTreeMethodSplitter/1.py").inputStream()
        )
        assertNotNull(node)
        assertFalse(node.wrappedNode.children.isEmpty())
    }
}
