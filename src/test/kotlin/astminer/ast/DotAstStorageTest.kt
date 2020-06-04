package astminer.ast

import astminer.common.createSmallTree
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals

class DotAstStorageTest {

    @Test
    fun testDotStorageOnSmallTree() {
        val root = createSmallTree()
        val storage = DotAstStorage("test_examples")
        storage.store(root, "entityId")

        storage.close()

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
    fun testLabelNormalization() {
        val label = "some/kind/of/random/path"
        val storage = DotAstStorage(".")
        val normalizedLabel = storage.normalizeAstLabel(label)

        assertEquals("some_kind_of_random_path", normalizedLabel)
    }

    @Test
    fun testBindingNormalization() {
        val label = "\$supposeToBeListener"
        val storage = DotAstStorage(".")
        val normalizedLabel = storage.normalizeAstLabel(label)

        assertEquals("_supposeToBeListener", normalizedLabel)
    }

    @Test
    fun testLabelWithCommaNormalization() {
        val labelWithComma = "some,bad,label"
        val storage = DotAstStorage(".")
        val normalizedLabel = storage.normalizeAstLabel(labelWithComma)

        assertEquals("some_bad_label", normalizedLabel)
    }

    @Test
    fun testSplittingFullPath() {
        val fullPath = "/path1/path2/path_3/path.4/file.name"
        val storage = DotAstStorage(".")
        val (path, fileName) = storage.splitFullPath(fullPath)

        assertEquals("/path1/path2/path_3/path.4", path)
        assertEquals("file.name", fileName)
    }

    @Test
    fun testSplittingFileName() {
        val fullPath = "file.name"
        val storage = DotAstStorage(".")
        val (path, fileName) = storage.splitFullPath(fullPath)

        assertEquals("", path)
        assertEquals("file.name", fileName)
    }

    @Test
    fun testFilepathNormalization() {
        // real life example
        val badFilepath = "interviews/Leet-Code/binary-search/pow(x,n).java"
        val storage = DotAstStorage(".")
        val normalizedFilepath = storage.normalizeFilepath(badFilepath)

        assertEquals("interviews/Leet-Code/binary-search/pow_x_n_.java", normalizedFilepath)
    }
}