package astminer.parse.gumtree.java

import astminer.common.model.FunctionInfo
import astminer.common.model.MethodInfo
import astminer.parse.gumtree.GumTreeNode
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

private fun createTree(filename: String): GumTreeNode {
    val parser = GumTreeJavaParser()
    return parser.parseInputStream(File(filename).inputStream()) as GumTreeNode
}

private fun createAndSplitTree(filename: String): Collection<FunctionInfo<GumTreeNode>> {
    return GumTreeJavaMethodSplitter().splitIntoMethods(createTree(filename))
}

class GumTreeJavaMethodSplitterTest {
    @Test
    fun testMethodExtraction1() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/1.java")

        assertEquals(1, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("fun", name)
            assertEquals("void", returnType)
            assertEquals("SingleFunction", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters.map { it.name }.toList())
            assertEquals(listOf("String[]", "int"), parameters.map { it.type }.toList())
        }

    }

    @Test
    fun testMethodExtraction2() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/2.java")

        assertEquals(1, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("main", name)
            assertEquals("void", returnType)
            assertEquals("InnerClass", enclosingElement?.name)
            assertEquals(listOf("args"), parameters.map { it.name }.toList())
            assertEquals(listOf("String[]"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun testMethodExtraction3() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/3.java")

        assertEquals(2, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("main", name)
            assertEquals("void", returnType)
            assertEquals("InnerClass", enclosingElement?.name)
            assertEquals(listOf("args"), parameters.map { it.name }.toList())
            assertEquals(listOf("String[]"), parameters.map { it.type }.toList())
        }
        with(methodInfos.last()) {
            assertEquals("fun", name)
            assertEquals("void", returnType)
            assertEquals("SingleMethodInnerClass", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters.map { it.name }.toList())
            assertEquals(listOf("String[]", "int"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun testMethodExtraction4() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/4.java")

        assertEquals(1, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("fun", name)
            assertEquals("int", returnType)
            assertEquals("SingleFunction", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters.map { it.name }.toList())
            assertEquals(listOf("int", "SingleFunction"), parameters.map { it.type }.toList())
        }
    }
}