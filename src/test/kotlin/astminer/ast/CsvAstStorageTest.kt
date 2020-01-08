package astminer.ast

import astminer.common.createSmallTree
import org.junit.Assert
import org.junit.Test

class CsvAstStorageTest {

    @Test
    fun testAstString() {
        val root = createSmallTree()
        val storage = CsvAstStorage()
        storage.store(root, "entityId")

        Assert.assertEquals("1 1{2 2{}3 3{4 4{}}}", storage.astString(root))
    }

}