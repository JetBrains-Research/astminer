package astminer.filters

import astminer.common.createBamboo
import astminer.common.toParseResult
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class FileFiltersTest {
    @Test
    fun `test TreeSizeFilter for 100 should exclude bamboo of length 101`() {
        val node = createBamboo(101).toParseResult()
        assertFalse { TreeSizeFilter(100).test(node) }
    }

    @Test
    fun `test TreeSizeFilter for 10 should not exclude bamboo of length 5`() {
        val node = createBamboo(5).toParseResult()
        assertTrue { TreeSizeFilter(10).test(node) }
    }
}
