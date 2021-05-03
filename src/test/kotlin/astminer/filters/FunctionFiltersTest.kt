package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FunctionFiltersTest {

    @Test
    fun `test modifiers filter should exclude function if it has the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("b", "c")
        }
        assertFalse { ModifierFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test modifiers filter should not exclude function if it does not have the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("c", "d")
        }
        assertTrue { ModifierFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test annotations filter should exclude function if it has the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("a", "c")
        }
        assertFalse { AnnotationFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test annotations filter should not exclude function if it does not have the excluded modifier`() {
        val excludedModifiers = listOf("a", "b")
        val functionInfo = object : FunctionInfo<Node> {
            override val modifiers: List<String> = listOf("y", "x")
        }
        assertTrue { AnnotationFilter(excludedModifiers).isFiltered(functionInfo) }
    }

    @Test
    fun `test constructor filter should exclude constructor functions`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val isConstructor = true
        }
        assertFalse { ConstructorFilter.isFiltered(functionInfo) }
    }

    @Test
    fun `test constructor filter should not exclude non-constructor functions`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val isConstructor = false
        }
        assertTrue { ConstructorFilter.isFiltered(functionInfo) }
    }

    @Test
    fun `test function name words number filter should not exclude function if maxWordsNumber is -1`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertTrue { FunctionNameWordsNumberFilter(-1).isFiltered(functionInfo) }
    }

    @Test
    fun `test function name words number filter for 50 should exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionNameWordsNumberFilter(50).isFiltered(functionInfo) }
    }

    @Test
    fun `test function name words number filter for 101 should not exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionNameWordsNumberFilter(101).isFiltered(functionInfo) }
    }

    @Test
    fun `test function any node words number filter should not exclude function if maxWordsNumber is -1`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertTrue { FunctionAnyNodeWordsNumberFilter(-1).isFiltered(functionInfo) }
    }

    @Test
    fun `test function any node words number filter for 50 should exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionAnyNodeWordsNumberFilter(50).isFiltered(functionInfo) }
    }

    @Test
    fun `test function any node words number filter for 101 should not exclude function with name of 100 words`() {
        val functionInfo = object : FunctionInfo<Node> {
            override val name = "Word".repeat(100)
        }
        assertFalse { FunctionAnyNodeWordsNumberFilter(101).isFiltered(functionInfo) }
    }

    @Test
    fun `test function any node words number filter for 2 should exlude function that has a child of 3 words`() {
        val root = AntlrNode("", null, "word")
        val child = AntlrNode("", root, "wordWordWord")
        root.setChildren(listOf(root))

        val functionInfo = object : FunctionInfo<Node> {
            override val root = root
        }
        assertFalse { FunctionAnyNodeWordsNumberFilter(2).isFiltered(functionInfo) }
    }


}