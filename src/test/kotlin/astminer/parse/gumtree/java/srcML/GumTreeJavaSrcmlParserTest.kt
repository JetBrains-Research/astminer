package astminer.parse.gumtree.java.srcML

import astminer.checkExecutable
import astminer.common.getProjectFilesWithExtension
import astminer.parseFiles
import org.junit.Assert
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

internal class GumTreeJavaSrcmlParserTest {
    @Test
    fun testNodeIsNotNull() {
        val parser = GumTreeJavaSrcmlParser()
        val file = File("src/test/resources/examples/1.java")

        val node = parser.parseFile(file)
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testProjectParsing() {
        val parser = GumTreeJavaSrcmlParser()
        val projectRoot = File("src/test/resources/examples")

        val trees = parser.parseFiles(getProjectFilesWithExtension(projectRoot, "java"))
        Assert.assertEquals("There is only 2 file with .java extension in 'testData/examples' folder", 2, trees.size)
        trees.forEach { Assert.assertNotNull("Parse tree for a valid file should not be null", it) }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun checkSrcmlInstalled() {
            Assume.assumeTrue(checkExecutable("srcml"))
        }
    }
}
