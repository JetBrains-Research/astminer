package astminer.common

import org.junit.Assert
import org.junit.Test

class TreeUtilTest {
    @Test
    fun testPostOrder() {
        val root = createDummyTree()
        val dataList = root.postOrderIterator().asSequence().map { it.getTypeLabel() }

        Assert.assertArrayEquals(arrayOf("4", "5", "6", "2", "7", "8", "3", "1"), dataList.toList().toTypedArray())
    }

    @Test
    fun testPreOrder() {
        val root = createDummyTree()
        val dataList = root.preOrderIterator().asSequence().map { it.getTypeLabel() }

        Assert.assertArrayEquals(arrayOf("1", "2", "4", "5", "6", "3", "7", "8"), dataList.toList().toTypedArray())
    }
}