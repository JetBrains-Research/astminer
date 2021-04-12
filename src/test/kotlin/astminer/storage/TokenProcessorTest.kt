package astminer.storage

import astminer.common.DummyNode
import org.junit.Assert
import org.junit.Test

internal class TokenProcessorTest {
    @Test
    fun `test leave original should return the unchanged token`() {
        val node = DummyNode("original unchanged token", mutableListOf())
        Assert.assertEquals("original unchanged token", TokenProcessor.LeaveOriginal.processToken(node))
    }
}
