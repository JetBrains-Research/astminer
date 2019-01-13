package miningtool.parse.antlr.java

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ANTLRJava8ParserTest {

    @Test
    fun testIntArrayCallParsing() {
        val parser = Java8Parser()
        val file = File("testData/arrayCalls/IntArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testStringArrayCallParsing() {
        val parser = Java8Parser()
        val file = File("testData/arrayCalls/StringArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testComplexArrayCallParsing() {
        val parser = Java8Parser()
        val file = File("testData/arrayCalls/ComplexArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testDoubleArrayCallParsing() {
        val parser = Java8Parser()
        val file = File("testData/arrayCalls/DoubleArrayInitialization.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }

    @Test
    fun testComplexFile() {
        val parser = Java8Parser()
        val file = File("testData/arrayCalls/ComplexTest.java")

        val node = parser.parse(FileInputStream(file))
        Assert.assertNotNull("Parse tree for a valid file should not be null", node)
    }
}