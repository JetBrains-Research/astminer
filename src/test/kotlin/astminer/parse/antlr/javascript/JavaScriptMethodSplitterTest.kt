package astminer.parse.antlr.javascript

import astminer.common.model.MethodInfo
import astminer.parse.antlr.SimpleNode
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

    var methodInfos: Collection<MethodInfo<SimpleNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree = parser.parseInputStream(File(testFilePath).inputStream())
        assertNotNull(testTree)
        methodInfos = methodSplitter.splitIntoMethods(testTree)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_METHODS, methodInfos.size, "Test file contains $N_METHODS methods")
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

        fun MethodInfo<SimpleNode>.getJsonInfo(): String {
            return "info : {" +
                    "name : ${name()}, " +
                    "args : ${methodParameters.map { it.name() }.joinToString(", ")}, " +
                    "enclosing element : ${enclosingElement.root?.getTypeLabel()?.getEnclosingElementType()}, " +
                    "enclosing element name : ${enclosingElementName()}" +
                    "}"
        }

        val actualJsonInfos = methodInfos.map { it.getJsonInfo() }.sorted()

        val text = File(testFilePath).readText()
        val expectedJsonInfos = Regex("info : \\{.*\\}").findAll(text).toList().map { it.value }.sorted()

        assertEquals(expectedJsonInfos, actualJsonInfos)
    }
}