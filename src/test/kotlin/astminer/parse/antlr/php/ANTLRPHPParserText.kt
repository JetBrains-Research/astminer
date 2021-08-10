package astminer.parse.antlr.php

import org.junit.Test
import java.io.File
import java.io.FileInputStream
import kotlin.test.assertNotNull

internal class ANTLRPHPParserText {

    @Test
    fun testNodeIsNotNull() {
        val parser = PHPParser()
        val file = File("src/test/resources/examples/1.php")
        val node = parser.parseInputStream(FileInputStream(file))
        assertNotNull(node)
    }
}
