package astminer.parse.antlr.javascript

import astminer.common.model.MethodInfo
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.decompressTypeLabel
import org.junit.Test
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class JavaScriptMethodSplitterTest {
    companion object {
        const val N_METHODS = 47
        const val testFilePath = "testData/methodSplitting/testMethodSplitting.js"
        val methodSplitter = JavaScriptMethodSplitter()
        val parser = JavaScriptParser()
    }

    var methodInfos: Collection<MethodInfo<SimpleNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree = parser.parse(File(testFilePath).inputStream())
        assertNotNull(testTree)
        methodInfos = methodSplitter.splitIntoMethods(testTree)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_METHODS, methodInfos.size, "Test file contains $N_METHODS methods")
    }

    @Test
    fun testValidMethodInfo() {
        fun MethodInfo<SimpleNode>.getEnclosingElementType(): String {
            return when {
                enclosingElement.root == null -> "null"
                enclosingElement.root!!.getTypeLabel().contains("functionDeclaration") -> "fun"
                enclosingElement.root!!.getTypeLabel().contains("classDeclaration") -> "class"
                enclosingElement.root!!.getTypeLabel().contains("methodDefinition") -> "method"
                enclosingElement.root!!.getTypeLabel().contains("variableDeclaration") -> "var"
                else -> ""
            }
        }

        fun MethodInfo<SimpleNode>.getJsonInfo(): String {
            return "info : {" +
                    "name : ${name()}, " +
                    "args : ${methodParameters.map { it.name() }.joinToString(", ")}, " +
                    "enclosing element : ${getEnclosingElementType()}, " +
                    "enclosing element name : ${enclosingElementName()}" +
                    "}"
        }

        val actualJsonInfos = methodInfos.map { it.getJsonInfo() }.sorted()

        val text = File(testFilePath).readText()
        val expectedJsonInfos = Regex("info : \\{.*\\}").findAll(text).toList().map { it.value }.sorted()

        assertEquals(expectedJsonInfos, actualJsonInfos)
    }
}