package astminer.parse.java

import astminer.common.getProjectFilesWithExtension
import org.junit.Assert
import org.junit.Test
import java.io.*

class GumTreeJavaParserTest {
    @Test
    fun testNodeIsNotNull() {
        val parser = GumTreeJavaParser()
        val file = File("src/test/resources/examples/1.java")

        val node = parser.parse(file).root
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testProjectParsing() {
        val parser = GumTreeJavaParser()
        val projectRoot = File("src/test/resources/examples")
        val trees = parser.parse(getProjectFilesWithExtension(projectRoot, "java")).map { it.root }
        Assert.assertEquals("There is only 2 file with .java extension in 'testData/examples' folder",2, trees.size)
        trees.forEach { Assert.assertNotNull("Parse tree for a valid file should not be null", it) }
    }
}