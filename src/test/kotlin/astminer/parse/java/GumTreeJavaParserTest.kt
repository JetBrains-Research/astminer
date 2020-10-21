package astminer.parse.java

import astminer.common.getProjectFilesWithExtension
import astminer.common.model.Node
import org.junit.Assert
import org.junit.Test
import java.io.*

class GumTreeJavaParserTest {
    @Test
    fun testNodeIsNotNull() {
        val parser = GumTreeJavaParser()
        val file = File("src/test/resources/examples/1.java")

        val node = parser.parseFile(file).root
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testProjectParsing() {
        val parser = GumTreeJavaParser()
        val projectRoot = File("src/test/resources/examples")
        
        val trees = mutableListOf<Node?>() 
        parser.parseFiles(getProjectFilesWithExtension(projectRoot, "java")) { trees.add(it.root) }
        Assert.assertEquals("There is only 2 file with .java extension in 'testData/examples' folder",2, trees.size)
        trees.forEach { Assert.assertNotNull("Parse tree for a valid file should not be null", it) }
    }
}