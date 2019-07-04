package astminer.parse.antlr.javascript

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRJSParserTest {

    @Test
    fun testNodeIsNotNull() {
        val parser = JSParser()
        val file = File("testData/examples/1.js")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

}