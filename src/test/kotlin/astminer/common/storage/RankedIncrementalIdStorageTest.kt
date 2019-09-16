package astminer.common.storage

import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class RankedIncrementalIdStorageTest {
    var storage = RankedIncrementalIdStorage<Int>()

    @BeforeTest
    fun initStorage() {
        storage = RankedIncrementalIdStorage()
    }

    @Test
    fun testPutItem() {
        val item = 42
        assertEquals(0, storage.getId(item))
        storage.record(item)
        assertEquals(1, storage.getId(item))
    }

    @Test
    fun testCount() {
        val item = 42
        val count = 10
        for (i in 1..count) {
            storage.record(item)
            assertEquals(i.toLong(), storage.getIdCount(storage.getId(item)))
        }
    }

    @Test
    fun testLookUpValue() {
        val item = 42
        storage.record(item)
        assertEquals(item, storage.lookUpValue(storage.getId(item)))
    }

    @Test
    fun testRankValues() {
        val items = listOf(1, 2, 3, 4, 5)
        val counts = listOf(4, 3, 2, 5, 1)
        val correctRanks = listOf(2, 3, 4, 1, 5)

        for (i in items.indices) {
            for (rep in 0 until counts[i]) {
                storage.record(items[i])
            }
        }

        storage.computeRanks()

        for (i in items.indices) {
            assertEquals(correctRanks[i].toLong(), storage.getKeyRank(items[i]))
        }
    }
}