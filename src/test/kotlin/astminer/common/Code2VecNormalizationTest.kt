package astminer.common

import astminer.common.model.Code2VecNormalization
import org.junit.Assert
import org.junit.Test

class Code2VecNormalizationTest {
    private val defaultToken = "EMPTY"

    @Test
    fun testPreOrder() {
        val root = createDummyTree()
        val dataList = root.preOrderIterator().asSequence().map { it.typeLabel }

        Assert.assertArrayEquals(arrayOf("1", "2", "4", "5", "6", "3", "7", "8"), dataList.toList().toTypedArray())
    }

    @Test
    fun testPostOrder() {
        val root = createDummyTree()
        val dataList = root.postOrderIterator().asSequence().map { it.typeLabel }

        Assert.assertArrayEquals(arrayOf("4", "5", "6", "2", "7", "8", "3", "1"), dataList.toList().toTypedArray())
    }

    @Test
    fun testNormalizeTokenCleaning() {
        val token = "   Token THAT  \n contains Whi\"t,es''pace characters!!!and pu.n.c.t.u.a.tion  \n"
        val expectedToken = "token" + "that" + "contains" + "whitespace" + "characters" + "and" + "punctuation"
        Assert.assertEquals(
            "All whitespace characters and punctuation should be removed, keeping only letters",
            expectedToken,
            Code2VecNormalization.normalizeSubToken(token, defaultToken)
        )
    }

    @Test
    fun testNormalizeTokenWithoutLetters() {
        val token = "* *\n"
        val expectedToken = "*_*"
        Assert.assertEquals(
            "Token without letters have whitespaces replaced with underscores",
            expectedToken,
            Code2VecNormalization.normalizeSubToken(token, defaultToken)
        )
    }

    @Test
    fun testNormalizeEmptyToken() {
        val token = "\n\n"
        val expectedToken = Code2VecNormalization.EMPTY_TOKEN
        Assert.assertEquals(
            "Token without letters have whitespaces replaced with underscores",
            expectedToken,
            Code2VecNormalization.normalizeSubToken(token, defaultToken)
        )
    }

    @Test
    fun testTokenSplit() {
        val token = "fun_withReallyLong_And_ComplicatedName"
        val expectedToken = listOf("fun", "with", "really", "long", "and", "complicated", "name")
        Assert.assertEquals(
            "Token with snake, camel and combined case should be split into list of its parts",
            expectedToken,
            Code2VecNormalization.splitToSubtokens(token)
        )
    }
}
