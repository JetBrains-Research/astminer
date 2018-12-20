package miningtool.parse.java

import org.junit.Assert
import org.junit.Test
import java.io.*

class GumTreeJavaParserTest {
    @Test
    fun testNodeIsNotNull() {
        val parser = GumTreeJavaParser()
        val file = File("testData/1.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }
}