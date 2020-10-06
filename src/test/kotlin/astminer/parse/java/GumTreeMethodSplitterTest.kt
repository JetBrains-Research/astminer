package astminer.parse.java

import astminer.common.model.MethodInfo
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

private fun createTree(filename: String): GumTreeJavaNode {
    val parser = GumTreeJavaParser()
    return parser.parseInputStream(File(filename).inputStream()) as GumTreeJavaNode
}

private fun createAndSplitTree(filename: String): Collection<MethodInfo<GumTreeJavaNode>> {
    return GumTreeMethodSplitter().splitIntoMethods(createTree(filename))
}

class GumTreeMethodSplitterTest {
    @Test
    fun testMethodExtraction1() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/1.java")

        assertEquals(1, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("fun", name())
            assertEquals("void", returnType())
            assertEquals("SingleFunction", enclosingElementName())
            assertEquals(listOf("args", "param"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("String[]", "int"), methodParameters.map { it.returnType() }.toList())
        }

    }

    @Test
    fun testMethodExtraction2() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/2.java")

        assertEquals(1, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("main", name())
            assertEquals("void", returnType())
            assertEquals("InnerClass", enclosingElementName())
            assertEquals(listOf("args"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("String[]"), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun testMethodExtraction3() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/3.java")

        assertEquals(2, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("main", name())
            assertEquals("void", returnType())
            assertEquals("InnerClass", enclosingElementName())
            assertEquals(listOf("args"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("String[]"), methodParameters.map { it.returnType() }.toList())
        }
        with(methodInfos.last()) {
            assertEquals("fun", name())
            assertEquals("void", returnType())
            assertEquals("SingleMethodInnerClass", enclosingElementName())
            assertEquals(listOf("args", "param"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("String[]", "int"), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun testMethodExtraction4() {
        val methodInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/4.java")

        assertEquals(1, methodInfos.size)
        with(methodInfos.first()) {
            assertEquals("fun", name())
            assertEquals("int", returnType())
            assertEquals("SingleFunction", enclosingElementName())
            assertEquals(listOf("args", "param"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("int", "SingleFunction"), methodParameters.map { it.returnType() }.toList())
        }
    }
}