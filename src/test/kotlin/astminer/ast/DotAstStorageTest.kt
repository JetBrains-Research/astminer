package astminer.ast

import astminer.common.createSmallTree
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class DotAstStorageTest {

    @Test
    fun testDotStorageOnSmallTree() {
        val root = createSmallTree()
        val storage = DotAstStorage()
        storage.store(root, "entityId")

        storage.save("test_examples")

        val trueLines = listOf(
                "digraph entityId {",
                "0 -- {1 2};",
                "1 -- {};",
                "2 -- {3};",
                "3 -- {};",
                "}"
        )
        val storageLines = File(File("test_examples", "asts"), "ast_0.dot").readLines()

        File("test_examples").deleteRecursively()

        assertEquals(trueLines, storageLines)
    }

    @Test
    fun testPathNormalization() {
        val label = "some/kind/of/random/path"
        val storage = DotAstStorage()
        val normalizedLabel = storage.normalizeAstLabel(label)

        assertEquals("some_kind_of_random_path", normalizedLabel)
    }

    @Test
    fun testBindingNormalization() {
        val label = "\$supposeToBeListener"
        val storage = DotAstStorage()
        val normalizedLabel = storage.normalizeAstLabel(label)

        assertEquals("_supposeToBeListener", normalizedLabel)
    }

}