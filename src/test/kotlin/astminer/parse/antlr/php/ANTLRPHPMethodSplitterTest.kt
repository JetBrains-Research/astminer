package astminer.parse.antlr.php

import astminer.common.model.EnclosingElementType
import org.junit.Test
import kotlin.test.BeforeTest
import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.javascript.JavaScriptFunctionSplitterTest
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ANTLRPHPMethodSplitterTest {
    companion object {
        const val N_METHODS = 18
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

    @Test
    fun testValidMethodInfo() {
        fun EnclosingElementType.getEnclosingElementType(): String {
            return when (this) {
                EnclosingElementType.Function -> "function"
                EnclosingElementType.Class -> "class"
                EnclosingElementType.Method -> "method"
                EnclosingElementType.VariableDeclaration -> "variable"
                else -> ""
            }
        }

        fun FunctionInfo<AntlrNode>.getJsonInfo(): String {
            return "info : {" +
                    "name : ${name}, " +
                    "args : ${parameters.joinToString(", ") { "${it.type} ${it.name}" }}, " +
                    "enclosing element : ${enclosingElement?.type?.getEnclosingElementType()}, " +
                    "}"
        }

        val actualJsonInfos = functionInfos.map { it.getJsonInfo() }.sorted()

        val text = File(JavaScriptFunctionSplitterTest.testFilePath).readText()
        val expectedJsonInfos = Regex("info : \\{.*\\}").findAll(text).toList().map { it.value }.sorted()

        assertEquals(expectedJsonInfos, actualJsonInfos)
    }
}