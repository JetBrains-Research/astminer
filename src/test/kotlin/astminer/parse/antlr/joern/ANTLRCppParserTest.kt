package astminer.parse.antlr.joern

import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import kotlin.test.Ignore

class ANTLRCppParserTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            cleanJoern()
            setupJoern()
        }

        @AfterClass
        @JvmStatic
        fun clean() {
            cleanJoern()
        }
    }

    @Ignore
    fun testNodeIsNotNull() {
        val parsedRoots = parseJoernAst("testData/examples/cpp/")
        Assert.assertEquals("Found 5 files", 5, parsedRoots.size)
        parsedRoots.forEach { Assert.assertNotNull("Parsed all 5 files", it) }
    }
}