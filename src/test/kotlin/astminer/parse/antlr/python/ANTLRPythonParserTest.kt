package astminer.parse.antlr.python

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRPythonParserTest {

    @Test
    fun testNodeIsNotNull() {
        val parser = PythonParser()
        val file = File("src/test/resources/examples/1.py")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testProjectParsing() {
        val parser = PythonParser()
        val projectRoot = File("src/test/resources/examples")
        val trees = parser.parseWithExtension(projectRoot, "py")
        Assert.assertEquals("There is only 1 file with .py extension in 'testData/examples' folder",1, trees.size)
        trees.forEach { Assert.assertNotNull("Parse tree for a valid file should not be null", it) }
    }
}