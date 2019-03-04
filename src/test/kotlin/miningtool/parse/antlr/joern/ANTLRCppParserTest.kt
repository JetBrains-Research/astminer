package miningtool.parse.antlr.joern

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRCppParserTest {

    @Test
    fun testNodeIsNotNull() {
        val parser = CppParser()
        for (i in 1..5) {
            val file = File("testData/examples/cpp/realExamples/$i.cpp")

            val node = parser.parse(FileInputStream(file))
            Assert.assertNotNull("Parse tree for a valid file should not be null", node)
        }
    }
}