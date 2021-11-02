package astminer.parse.gumtree.java.srcML

import astminer.checkExecutable
import astminer.common.model.FunctionInfo
import astminer.parse.gumtree.GumTreeNode
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class GumTreeJavaSrcmlFunctionSplitterTest {
    private fun createTree(filename: String): GumTreeNode =
        GumTreeJavaSrcmlParser().parseFile(File(filename))

    private fun createAndSplitTree(filename: String): Collection<FunctionInfo<GumTreeNode>> =
        GumTreeJavaSrcmlFunctionSplitter().splitIntoFunctions(createTree(filename), filename)

    @Test
    fun testMethodExtraction1() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/1.java")

        assertEquals(1, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("fun", name)
            assertEquals("void", returnType)
            assertEquals("SingleFunction", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters?.map { it.name })
            assertEquals(listOf("String[]", "int"), parameters?.map { it.type })
        }
    }

    @Test
    fun testMethodExtraction2() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/2.java")

        assertEquals(1, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("main", name)
            assertEquals("void", returnType)
            assertEquals("InnerClass", enclosingElement?.name)
            assertEquals(listOf("args"), parameters?.map { it.name })
            assertEquals(listOf("String[]"), parameters?.map { it.type })
        }
    }

    @Test
    fun testMethodExtraction3() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/3.java")

        assertEquals(2, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("main", name)
            assertEquals("void", returnType)
            assertEquals("InnerClass", enclosingElement?.name)
            assertEquals(listOf("args"), parameters?.map { it.name })
            assertEquals(listOf("String[]"), parameters?.map { it.type })
        }
        with(functionInfos.last()) {
            assertEquals("fun", name)
            assertEquals("void", returnType)
            assertEquals("SingleMethodInnerClass", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters?.map { it.name })
            assertEquals(listOf("String[]", "int"), parameters?.map { it.type })
        }
    }

    @Test
    fun testMethodExtraction4() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/4.java")

        assertEquals(1, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("fun", name)
            assertEquals("int", returnType)
            assertEquals("SingleFunction", enclosingElement?.name)
            assertEquals(listOf("args", "param"), parameters?.map { it.name })
            assertEquals(listOf("int", "SingleFunction"), parameters?.map { it.type })
        }
    }

    @Test
    fun testMethodExtraction5() {
        val functionInfos = createAndSplitTree("src/test/resources/gumTreeMethodSplitter/5.java")

        assertEquals(2, functionInfos.size)
        with(functionInfos.first()) {
            assertEquals("someDeprecatedFun", name)
            assertEquals("String", returnType)
            assertEquals("AnnotatedFunction", enclosingElement?.name)
            assertEquals(setOf("Deprecated", "SuppressWarnings"), annotations?.toSet())
            assertFalse(isBlank())
        }
        with(functionInfos.last()) {
            assertEquals("function", name)
            assertEquals("int", returnType)
            assertEquals("someAbstractClass", enclosingElement?.name)
            assertEquals(setOf("public", "abstract"), modifiers?.toSet())
            assertTrue(isBlank())
        }
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun checkTreeSitterInstalled() {
            Assume.assumeTrue(checkExecutable("srcml"))
        }
    }
}
