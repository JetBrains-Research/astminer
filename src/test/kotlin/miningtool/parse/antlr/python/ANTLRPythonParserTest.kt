package miningtool.parse.antlr.python

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRPythonParserTest {

    @Test
    fun testNodeIsNotNull() {
        val parser = PythonParser()
        val file = File("testData/1.py")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }
}