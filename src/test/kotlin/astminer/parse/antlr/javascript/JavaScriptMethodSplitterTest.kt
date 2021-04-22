package astminer.parse.antlr.javascript

import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class JavaScriptMethodSplitterTest {
    companion object {
        const val N_METHODS = 47
        const val testFilePath = "src/test/resources/methodSplitting/testMethodSplitting.js"
        val methodSplitter = JavaScriptMethodSplitter()
        val parser = JavaScriptParser()
    }

    var functionInfos: Collection<FunctionInfo<AntlrNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree = parser.parseInputStream(File(testFilePath).inputStream())
        assertNotNull(testTree)
        functionInfos = methodSplitter.splitIntoMethods(testTree)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_METHODS, functionInfos.size, "Test file contains $N_METHODS methods")
    }

    @Test
    fun testValidMethodInfo() {
        fun String.getEnclosingElementType(): String {
            return when {
                "functionDeclaration" in this -> "fun"
                "classDeclaration" in this -> "class"
                "methodDefinition" in this -> "method"
                "variableDeclaration" in this -> "var"
                else -> ""
            }
        }

        fun FunctionInfo<AntlrNode>.getJsonInfo(): String {
            return "info : {" +
                    "name : ${name}, " +
                    "args : ${parameters.map { it.name }.joinToString(", ")}, " +
                    "enclosing element : ${enclosingElement?.getTypeLabel()?.getEnclosingElementType()}, " +
                    "enclosing element name : ${className}" +
                    "}"
        }

        val actualJsonInfos = functionInfos.map { it.getJsonInfo() }.sorted()

        val text = File(testFilePath).readText()
        val expectedJsonInfos = Regex("info : \\{.*\\}").findAll(text).toList().map { it.value }.sorted()

        assertEquals(expectedJsonInfos, actualJsonInfos)
    }
}