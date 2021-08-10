package astminer.parse.antlr

import astminer.parse.antlr.java.JavaParser
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class AntrlUtilTest {
    @Test
    fun compressTreeTest() {
        val parser = JavaParser()
        val file = File("src/test/resources/methodSplitting/testMethodSplitting.java")

        val node = parser.parseInputStream(FileInputStream(file))
        var adoptedNodesSize = 0
        node.preOrder().forEach { curNode ->
            adoptedNodesSize += curNode.children.filter { it.parent != curNode }.size
        }
        Assert.assertEquals("There should be no children with different parent", 0, adoptedNodesSize)
    }
}
