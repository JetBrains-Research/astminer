package astminer.parse.python

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GumTreePythonParserTest {
    private val parser: GumTreePythonParser = GumTreePythonParser()
    private val testFolder = File(".python_parser_test_tmp")
    private val testFile = testFolder.resolve("test_file.py")

    @Before
    fun mkdir() {
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

    @Test(expected = Test.None::class)
    fun invalidCode() {
        testFile.writeText("INVALID PYTHON CODE")
        val node = parser.parseInputStream(testFile.inputStream())
        assertNull(node)
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
