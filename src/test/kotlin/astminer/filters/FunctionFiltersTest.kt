package astminer.filters

import astminer.common.createBamboo
import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FunctionFiltersTest {
    val a = ConstructorFilter
    @Test
    fun `test ModifierFilter should exclude function if it has the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("b", "c")
        }
        assertFalse { ModifierFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test ModifierFilter should not exclude function if it does not have the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("c", "d")
        }
        assertTrue { ModifierFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test AnnotationFilter should exclude function if it has the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("a", "c")
        }
        assertFalse { AnnotationFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test AnnotationFilter should not exclude function if it does not have the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("y", "x")
        }
        assertTrue { AnnotationFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test ConstructorFilter should exclude constructor functions`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val isConstructor = true
        }
        assertFalse { ConstructorFilter.isFiltered(functionInfo) }
    }

    @Test
    fun `test ConstructorFilter should not exclude non-constructor functions`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val isConstructor = false
        }
        assertTrue { ConstructorFilter.isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionNameWordsNumberFilter should not exclude function if maxWordsNumber is -1`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertTrue { FunctionNameWordsNumberFilter(-1).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionNameWordsNumberFilter for 50 should exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionNameWordsNumberFilter(50).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionNameWordsNumberFilter for 101 should not exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionNameWordsNumberFilter(101).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionAnyNodeWordsNumberFilter should not exclude function if maxWordsNumber is -1`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertTrue { FunctionAnyNodeWordsNumberFilter(-1).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionAnyNodeWordsNumberFilter for 50 should exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionAnyNodeWordsNumberFilter(50).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionAnyNodeWordsNumberFilter for 101 should not exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionAnyNodeWordsNumberFilter(101).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionAnyNodeWordsNumberFilter for 2 should exclude function that has a child of 3 words`() {
        val root = AntlrNode("", null, "word")
        val child = AntlrNode("", root, "wordWordWord")
        root.setChildren(listOf(child))

        val functionInfo = object : FunctionInfo<Node> {
            override val root = root
        }
        assertFalse { FunctionAnyNodeWordsNumberFilter(2).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionTreeSizeFilter for 100 should exclude bamboo of length 101`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val root = createBamboo(101)
        }
        assertFalse { FunctionTreeSizeFilter(101).isFiltered(functionInfo) }
    }

    @Test
    fun `test FunctionTreeSizeFilter for 10 should not exclude bamboo of length 5`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val root = createBamboo(5)
        }
        assertTrue { FunctionTreeSizeFilter(10).isFiltered(functionInfo) }
    }
}