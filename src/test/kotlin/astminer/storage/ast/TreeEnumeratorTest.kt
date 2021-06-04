package astminer.storage.ast

import astminer.common.DummyNode
import astminer.common.createBamboo
import astminer.common.createDummyTree
import astminer.common.createSmallTree
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class TreeEnumeratorTest {
    lateinit var treeEnumerator: TreeEnumerator

    @Before
    fun init() {
        treeEnumerator = TreeEnumerator()
    }

    private data class EnumeratedResult(val id: Int, val typeLabel: String, val children: List<Int> = emptyList())

    private fun enumerate(node: DummyNode): List<EnumeratedResult> {
        val enumeratedNodes = treeEnumerator.enumerate(node)
        return enumeratedNodes.map { EnumeratedResult(it.id, it.node.typeLabel, it.children.map { child -> child.id }) }
    }

    @Test
    fun `test for 1 node`() {
        val node = DummyNode("a")
        val expected = listOf(EnumeratedResult(0, "a"))
        assertEquals(expected, enumerate(node))
    }

    @Test
    fun `test should be reusable`() {
        treeEnumerator.enumerate(DummyNode("something previous"))
        val node = DummyNode("a")
        val expected = listOf(EnumeratedResult(0, "a"))
        assertEquals(expected, enumerate(node))
    }

    @Test
    fun `test on small bamboo`() {
        val bamboo = createBamboo(10)
        val expected = List(10) { i ->
            EnumeratedResult(i, (10 - i).toString(), if (i == 0) emptyList() else listOf(i - 1))
        }
        assertEquals(expected, enumerate(bamboo))
    }

    @Test
    fun `test on big bamboo`() {
        val bamboo = createBamboo(1000)
        val expected = List(1000) { i ->
            EnumeratedResult(i, (1000 - i).toString(), if (i == 0) emptyList() else listOf(i - 1))
        }
        assertEquals(expected, enumerate(bamboo))
    }

    @Test
    fun `test on very small dummy tree`() {
        val node = createSmallTree()
        val expected = listOf(
            EnumeratedResult(0, "2"),
            EnumeratedResult(1, "4"),
            EnumeratedResult(2, "3", listOf(1)),
            EnumeratedResult(3, "1", listOf(0, 2))
        )
        assertEquals(expected, enumerate(node))
    }

    @Test
    fun `test on small dummy tree`() {
        val node = createDummyTree()
        val expected = listOf(
            EnumeratedResult(0, "4"),
            EnumeratedResult(1, "5"),
            EnumeratedResult(2, "6"),
            EnumeratedResult(3, "2", listOf(0, 1, 2)),
            EnumeratedResult(4, "7"),
            EnumeratedResult(5, "8"),
            EnumeratedResult(6, "3", listOf(4, 5)),
            EnumeratedResult(7, "1", listOf(3, 6))
        )
        assertEquals(expected, enumerate(node))
    }
}
