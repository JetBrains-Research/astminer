package astminer.ast

import astminer.common.createSmallTree
import org.junit.Assert
import org.junit.Test

class CsvAstStorageTest {

    @Test
    fun testAstString() {
        val root = createSmallTree()
        val storage = CsvAstStorage()
        storage.init("/Users/admin/desktop/astminer")
        storage.store(root, "entityId")
        storage.save()
        Assert.assertEquals(storage.astString(root), "1 1{2 2{}3 3{4 4{}}}")
    }
}