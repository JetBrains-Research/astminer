package astminer.storage

import astminer.common.DEFAULT_TOKEN
import astminer.common.DummyNode
import astminer.common.setTechnicalToken
import org.junit.Assert
import org.junit.Test


internal class TokenProcessorTest {
    private fun normalizeToken(token: String): String {
        val node = DummyNode(token, mutableListOf())
        return TokenProcessor.Normalize.getPresentableToken(node)
    }

    private fun splitToken(token: String): String {
        val node = DummyNode(token, mutableListOf())
        return TokenProcessor.Split.getPresentableToken(node)
    }

    @Test
    fun testNormalizeTokenCleaning() {
        val token = "   Token THAT  \n contains Whi\"t,es''pace characters!!!and pu.n.c.t.u.a.tion  \n"
        val expectedToken = "token" + "that" + "contains" + "whitespace" + "characters" + "and" + "punctuation"
        Assert.assertEquals(
            "All whitespace characters and punctuation should be removed, keeping only letters",
            expectedToken,
            normalizeToken(token)
        )
    }

    @Test
    fun testNormalizeTokenWithoutLetters() {
        val token = "* *\n"
        val expectedToken = "*_*"
        Assert.assertEquals(
            "Token without letters have whitespaces replaced with underscores",
            expectedToken,
            normalizeToken(token)
        )
    }

    @Test
    fun testNormalizeEmptyToken() {
        val token = "\n\n"
        val expectedToken = DEFAULT_TOKEN
        Assert.assertEquals(
            "Token without letters have whitespaces replaced with underscores",
            expectedToken,
            normalizeToken(token)
        )
    }

    @Test
    fun testTokenSplit() {
        val token = "fun_withReallyLong_And_ComplicatedName"
        val expectedToken = "fun|with|really|long|and|complicated|name"
        Assert.assertEquals(
            "Token with snake, camel and combined case should be split into list of its parts",
            expectedToken,
            splitToken(token)
        )
    }

    @Test
    fun `test Normalize respects technical token`() {
        val node = DummyNode("tokenName", mutableListOf())
        node.setTechnicalToken("technical token")
        Assert.assertEquals("technical token", TokenProcessor.Normalize.getPresentableToken(node))
    }

    @Test
    fun `test Split respects technical token`() {
        val node = DummyNode("tokenName", mutableListOf())
        node.setTechnicalToken("technical token")
        Assert.assertEquals("technical token", TokenProcessor.Split.getPresentableToken(node))
    }
}