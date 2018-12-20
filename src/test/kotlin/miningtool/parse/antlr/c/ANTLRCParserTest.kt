package miningtool.parse.antlr.c

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRCParserTest {
    @Test
    fun testNodeIsNotNull() {
        val parser = ANTLRCParser()
        val file = File("testData/1.c")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }
}