package astminer.parse.antlr.php

import org.junit.Test
import kotlin.test.BeforeTest
import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ANTLRPHPMethodSplitterTest {
    companion object {
        const val N_METHODS = 4
        const val testFilePath = "src/test/resources/methodSplitting/testMethodSplitting.php"
        val functionSplitter = PHPMethodSplitter()
        val parser = PHPParser()
    }

    private var functionInfos: Collection<FunctionInfo<AntlrNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree = parser.parseInputStream(File(testFilePath).inputStream())
        assertNotNull(testTree)
        functionInfos = functionSplitter.splitIntoFunctions(testTree)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_METHODS, functionInfos.size, "Test file contains $N_METHODS methods")
    }

}