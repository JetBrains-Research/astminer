package astminer.parse.antlr.java

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRJavaParserTest {

    @Test
    fun testIntArrayCallParsing() {
        val parser = JavaParser()
        val file = File("src/test/resources/testData/arrayCalls/IntArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testStringArrayCallParsing() {
        val parser = JavaParser()
        val file = File("src/test/resources/testData/arrayCalls/StringArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testComplexArrayCallParsing() {
        val parser = JavaParser()
        val file = File("src/test/resources/testData/arrayCalls/ComplexArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testDoubleArrayCallParsing() {
        val parser = JavaParser()
        val file = File("src/test/resources/testData/arrayCalls/DoubleArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testComplexFile() {
        val parser = JavaParser()
        val file = File("src/test/resources/testData/arrayCalls/ComplexTest.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testProjectParsing() {
        val parser = JavaParser()
        val projectRoot = File("src/test/resources/testData/arrayCalls")
        val trees = parser.parseWithExtension(projectRoot, "java")
        Assert.assertEquals("There is only 5 file with .java extension in 'testData/arrayCalls' folder",5, trees.size)
        trees.forEach { Assert.assertNotNull("Parse tree for a valid file should not be null", it) }
    }
}