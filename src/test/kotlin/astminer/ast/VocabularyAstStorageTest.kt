package astminer.ast

import astminer.common.createSmallTree
import org.junit.Assert
import org.junit.Test

class VocabularyAstStorageTest {

    @Test
    fun testAstString() {
        val root = createSmallTree()
        val storage = VocabularyAstStorage()
        storage.store(root, "entityId")

        Assert.assertEquals(storage.astString(root), "1 1{2 2{}3 3{4 4{}}}")
    }

}