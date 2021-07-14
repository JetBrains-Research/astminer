package astminer.parse.javaparser

import astminer.common.model.FunctionInfo
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.BeforeTest

internal class JavaparserMethodSplitterTest {

    companion object {
        const val FILE_PATH = "src/test/resources/methodSplitting/testMethodSplitting.java"
        const val N_FUNCTIONS = 10
        val functionSplitter = JavaparserMethodSplitter()
        val parser = JavaParserParseWrapper()
    }

    var functionInfos: Collection<FunctionInfo<JavaParserNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree =  parser.parseInputStream(File(FILE_PATH).inputStream())
        assertNotNull(testTree)
        functionInfos = functionSplitter.splitIntoFunctions(testTree, FILE_PATH)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_FUNCTIONS, functionInfos.size, "Test file contains $N_FUNCTIONS methods")
    }
}