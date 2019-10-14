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
                "graph entityId {",
                "2 -- {0 1};",
                "0 -- {};",
                "1 -- {3};",
                "3 -- {};",
                "}"
        )
        val storageLines = File(File("test_examples", "asts"), "ast_0.dot").readLines()

        File("test_examples").deleteRecursively()

        assertEquals(trueLines, storageLines)
    }

}