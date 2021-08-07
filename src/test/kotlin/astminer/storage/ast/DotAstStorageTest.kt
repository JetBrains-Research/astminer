package astminer.storage.ast

import astminer.common.*
import org.junit.Test
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.assertEquals

class DotAstStorageTest {
    private fun testOnTree(root: DummyNode, expectedLines: List<String>) {
        DotAstStorage("test_examples").use { storage ->
            storage.store(root.labeledWith("entityId"))
        }

        val storageLines = File(File("test_examples/data", "asts"), "ast_0.dot").readLines()

        File("test_examples").deleteRecursively()

        assertEquals(expectedLines, storageLines)
    }

    private fun getBambooLines(size: Int): List<String> {
        val lines = mutableListOf<String>()
        lines.add("digraph entityId {")
        for (i in 0..size - 2) {
            lines.add("$i -- {${i + 1}};")
        }
        lines.add("${size - 1} -- {};")
        lines.add("}")
        return lines
    }

    @AfterTest
    fun removeOutput() {
        File(OUTPUT_FOLDER).deleteRecursively()
    }

    @Test
    fun testDotStorageOnSmallTree() {
        val root = createSmallTree()
        val storage = DotAstStorage(OUTPUT_FOLDER)
        storage.store(root.labeledWith("entityId"))

        storage.close()

        val trueLines = listOf(
            "digraph entityId {",
            "0 -- {1 2};",
            "1 -- {};",
            "2 -- {3};",
            "3 -- {};",
            "}"
        )
        val storageLines = File(File("$OUTPUT_FOLDER/data", "asts"), "ast_0.dot").readLines()
        assertEquals(trueLines, storageLines)
    }

    @Test
    fun `test dot storage on dummy tree`() {
        val trueLines = listOf(
            "digraph entityId {",
            "0 -- {1 2};",
            "1 -- {3 4 5};",
            "3 -- {};",
            "4 -- {};",
            "5 -- {};",
            "2 -- {6 7};",
            "6 -- {};",
            "7 -- {};",
            "}"
        )

        testOnTree(createDummyTree(), trueLines)
    }

    @Test
    fun `test dot storage on small bamboo`() {
        testOnTree(createBamboo(10), getBambooLines(10))
    }

    @Test
    fun `test dot storage on big bamboo`() {
        testOnTree(createBamboo(100), getBambooLines(100))
    }

    @Test
    fun testLabelNormalization() {
        val label = "some/kind/of/random/path"
        val storage = DotAstStorage(OUTPUT_FOLDER)
        val normalizedLabel = storage.normalizeAstLabel(label)

        assertEquals("some_kind_of_random_path", normalizedLabel)
    }

    @Test
    fun testBindingNormalization() {
        val label = "\$supposeToBeListener"
        val storage = DotAstStorage(OUTPUT_FOLDER)
        val normalizedLabel = storage.normalizeAstLabel(label)

        assertEquals("_supposeToBeListener", normalizedLabel)
    }

    @Test
    fun testLabelWithCommaNormalization() {
        val labelWithComma = "some,bad,label"
        val storage = DotAstStorage(OUTPUT_FOLDER)
        val normalizedLabel = storage.normalizeAstLabel(labelWithComma)

        assertEquals("some_bad_label", normalizedLabel)
    }

    @Test
    fun testSplittingFullPath() {
        val fullPath = "/path1/path2/path_3/path.4/file.name"
        val storage = DotAstStorage(OUTPUT_FOLDER)
        val (path, fileName) = storage.splitFullPath(fullPath)

        assertEquals("/path1/path2/path_3/path.4", path)
        assertEquals("file.name", fileName)
    }

    @Test
    fun testSplittingFileName() {
        val fullPath = "file.name"
        val storage = DotAstStorage(OUTPUT_FOLDER)
        val (path, fileName) = storage.splitFullPath(fullPath)

        assertEquals("", path)
        assertEquals("file.name", fileName)
    }

    @Test
    fun testFilepathNormalization() {
        // real life example
        val badFilepath = "interviews/Leet-Code/binary-search/pow(x,n).java"
        val storage = DotAstStorage(OUTPUT_FOLDER)
        val normalizedFilepath = storage.normalizeFilepath(badFilepath)

        assertEquals("interviews/Leet-Code/binary-search/pow_x_n_.java", normalizedFilepath)
    }

    companion object {
        private const val OUTPUT_FOLDER = "test_output"
    }
}
