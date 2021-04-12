package astminer.storage.ast

import astminer.common.createSmallTree
import astminer.common.labeledWith
import astminer.storage.ast.CsvAstStorage
import org.junit.Assert
import org.junit.Test

class CsvAstStorageTest {

    @Test
    fun testAstString() {
        val root = createSmallTree()
        val storage = CsvAstStorage(".")
        storage.store(root.labeledWith("entityId"))

        Assert.assertEquals(storage.astString(root), "1 1{2 2{}3 3{4 4{}}}")
    }

}