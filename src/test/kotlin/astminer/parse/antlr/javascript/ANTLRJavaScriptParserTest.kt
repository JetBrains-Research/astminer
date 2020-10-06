package astminer.parse.antlr.javascript

import org.junit.Test
import java.io.File
import java.io.FileInputStream
import kotlin.test.assertNotNull

class ANTLRJavaScriptParserTest {

    @Test
    fun testNodeIsNotNull() {
        val parser = JavaScriptParser()
        val file = File("src/test/resources/examples/1.js")
        val node = parser.parseInputStream(FileInputStream(file))
        assertNotNull(node, "Parse tree for a valid file should not be null")
    }

}