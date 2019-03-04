package miningtool.parse.java

import org.junit.Assert
import org.junit.Test
import java.io.File

private fun createTree(filename: String): GumTreeJavaNode {
    val parser = GumTreeJavaParser()
    return parser.parse(File(filename).inputStream()) as GumTreeJavaNode
}

private fun createAndSplitTree(filename: String): Collection<GumTreeJavaNode> {
    return GumTreeMethodSplitter().split(createTree(filename))
}

class GumTreeMethodSplitterTest {
    @Test
    fun testMethodExtraction1() {
        val methodNodes = createAndSplitTree("testData/gumTreeMethodSplitter/1.java")

        Assert.assertEquals(1, methodNodes.size)
        Assert.assertEquals(MethodInfo("SingleFunction", "fun", listOf("String[]", "int")), methodNodes.first().getMethodInfo())
    }

    @Test
    fun testMethodExtraction2() {
        val methodNodes = createAndSplitTree("testData/gumTreeMethodSplitter/2.java")

        Assert.assertEquals(1, methodNodes.size)
        Assert.assertEquals(MethodInfo("InnerClass", "main", listOf("String[]")), methodNodes.first().getMethodInfo())
    }

    @Test
    fun testMethodExtraction3() {
        val methodNodes = createAndSplitTree("testData/gumTreeMethodSplitter/3.java")

        Assert.assertEquals(2, methodNodes.size)
        Assert.assertTrue(methodNodes.map { it.getMethodInfo() }.contains(MethodInfo("InnerClass","main", listOf("String[]"))))
        Assert.assertTrue(methodNodes.map { it.getMethodInfo() }.contains(MethodInfo("SingleMethodInnerClass", "fun", listOf("String[]", "int"))))
    }

    @Test
    fun testMethodExtraction4() {
        val methodNodes = createAndSplitTree("testData/gumTreeMethodSplitter/4.java")

        Assert.assertEquals(1, methodNodes.size)
        Assert.assertEquals(MethodInfo("SingleFunction", "fun", listOf("int", "int")), methodNodes.first().getMethodInfo())
    }
}