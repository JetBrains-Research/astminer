package astminer.parse.antlr.php

import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ANTLRPHPFunctionSplitterTest {

    private var functionInfos: Collection<FunctionInfo<AntlrNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree = parser.parseInputStream(File(testFilePath).inputStream())
        assertNotNull(testTree)
        functionInfos = functionSplitter.splitIntoFunctions(testTree, testFilePath)
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

        fun FunctionInfo<AntlrNode>.getJsonInfo(): String = listOf(
            "info : {",
            "name: $name, ",
            "args: ${parameters?.joinToString(", ") {
                listOfNotNull(it.type, it.name).joinToString(" ")
            }}, ",
            "enclosing element: ${enclosingElement?.type?.getEnclosingElementType()}, ",
            "enclosing element name: ${enclosingElement?.name}, ",
            "return type: $returnType",
            "}"
        ).joinToString("")

        val actualJsonInfos = functionInfos.map { it.getJsonInfo() + '\n' }.sorted()

        val text = File(testFilePath).readText()
        val expectedJsonInfos = Regex("info : \\{.*}").findAll(text).toList().map { it.value + '\n' }.sorted()

        assertEquals(expectedJsonInfos, actualJsonInfos)
    }

    companion object {
        const val N_METHODS = 18
        const val testFilePath = "src/test/resources/methodSplitting/testMethodSplitting.php"
        val functionSplitter = PHPFunctionSplitter()
        val parser = PHPParser()
    }
}
