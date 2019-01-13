package miningtool.parse.antlr.java

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRJavaParserTest {

    @Test
    fun testIntArrayCallParsing() {
        val parser = JavaParser()
        val file = File("testData/IntArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testStringArrayCallParsing() {
        val parser = JavaParser()
        val file = File("testData/StringArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testComplexArrayCallParsing() {
        val parser = JavaParser()
        val file = File("testData/ComplexArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testDoubleArrayCallParsing() {
        val parser = JavaParser()
        val file = File("testData/DoubleArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }
}