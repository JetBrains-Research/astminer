package astminer.parse.javaparser

import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class JavaparserMethodSplitterTest {
    @Test
    fun testValidSplitting() {
        assertEquals(N_FUNCTIONS, functionInfos.size, "Test file contains $N_FUNCTIONS methods")
    }

    private fun findFunction(name: String): FunctionInfo<JavaParserNode> {
        val function = functionInfos.find { it.name == name }
        assertNotNull(function)
        return function
    }

    @Test
    fun testNoParameters() {
        val methodNoParameters = functionInfos.find { it.name == "functionWithNoParameters" }
        assertNotNull(methodNoParameters)
        assertEquals(0, methodNoParameters.parameters?.size)
    }

    @Test
    fun testOneParameter() {
        val methodOneParameter = functionInfos.find { it.name == "functionWithOneParameter" }
        assertNotNull(methodOneParameter)
        val parameters = checkNotNull(methodOneParameter.parameters)
        assertEquals(1, parameters.size)
        val parameter = parameters[0]
        assertEquals("p1", parameter.name)
        assertEquals("int", parameter.type)
    }

    @Test
    fun testThreeParameters() {
        val methodThreeParameters = functionInfos.find { it.name == "functionWithThreeParameters" }
        assertNotNull(methodThreeParameters)
        val parameters = checkNotNull(methodThreeParameters.parameters)
        assertEquals(3, parameters.size)
        val methodTypes = listOf("Class", "String[][]", "int[]")
        for (i in 0 until 3) {
            val parameter = parameters[i]
            assertEquals("p${i + 1}", parameter.name)
            assertEquals(methodTypes[i], parameter.type)
        }
    }

    @Test
    fun testWeirdArrayParameter() {
        val methodWeirdArrayParameter = functionInfos.find { it.name == "functionWithStrangeArrayParameter" }
        assertNotNull(methodWeirdArrayParameter)
        val parameters = checkNotNull(methodWeirdArrayParameter.parameters)
        assertEquals(1, parameters.size)
        val weirdParameter = parameters[0]
        assertEquals("arr", weirdParameter.name)
        assertEquals("int[]", weirdParameter.type)
    }

    @Test
    fun testFunctionInClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass1" }
        assertNotNull(methodClass)
        assertEquals(EnclosingElementType.Class, methodClass.enclosingElement?.type)
        assertEquals("Class1", methodClass.enclosingElement?.name)
    }

    @Test
    fun testFunctionInNestedClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass2" }
        assertNotNull(methodClass)
        assertEquals(EnclosingElementType.Class, methodClass.enclosingElement?.type)
        assertEquals("Class2", methodClass.enclosingElement?.name)
    }

    private fun testModifiers(functionName: String, expectedModifiers: Set<String>) {
        val function = functionInfos.find { it.name == functionName }
        assertNotNull(function)
        val actualModifiers = function.modifiers
        assertNotNull(actualModifiers)
        assertEquals(expected = expectedModifiers, actual = actualModifiers.toSet())
    }

    @Test
    fun testFunctionWithOneModifier() {
        testModifiers("abstractFunctionReturningInt", setOf("abstract"))
    }

    @Test
    fun testFunctionWithMultipleModifiers() {
        testModifiers("staticFunctionReturningString", setOf("static", "public", "final"))
    }

    @Test
    fun testFunctionWithNoBodyIsBlank() {
        val blankFunction = functionInfos.find { it.name == "abstractFunctionReturningInt" }
        assertNotNull(blankFunction)
        assertTrue(blankFunction.isBlank())
    }

    @Test
    fun testFunctionWithEmptyBodyIsBlank() {
        val blankFunction = functionInfos.find { it.name == "functionReturningVoid" }
        assertNotNull(blankFunction)
        assertTrue(blankFunction.isBlank())
    }

    private fun testAnnotationsMatches(functionName: String, expectedAnnotations: Set<String>) {
        val actualAnnotations = findFunction(functionName).annotations
        assertNotNull(actualAnnotations)
        assertEquals(expectedAnnotations, actualAnnotations.toSet())
    }

    @Test
    fun testOneAnnotation() {
        testAnnotationsMatches("deprecatedFunction", setOf("Deprecated"))
    }

    @Test
    fun testMultipleAnnotations() {
        testAnnotationsMatches("functionWithAnnotations", setOf("Deprecated", "SuppressWarnings"))
    }

    @Test
    fun testModifiersAndAnnotation() {
        testAnnotationsMatches("functionWithModifiersAndAnnotations", setOf("Deprecated"))
    }

    @Test
    fun testPositions() {
        assertTrue(
            functionInfos.mapNotNull { it.root.range }.zip(functionLinePositions).all {
                val actualStart = it.first.start.line
                val actualEnd = it.first.end.line
                val expectedStart = it.second.first
                val expectedEnd = it.second.second
                (actualStart..actualEnd).intersect(expectedStart..expectedEnd).isNotEmpty()
            }
        )
    }

    companion object {
        private const val FILE_PATH = "src/test/resources/methodSplitting/testMethodSplitting.java"
        const val N_FUNCTIONS = 15
        private val functionSplitter = JavaparserMethodSplitter()
        val parser = JavaParserParseWrapper()
        var functionInfos: Collection<FunctionInfo<JavaParserNode>> = listOf()
        val functionLinePositions = listOf(
            2 to 2,
            4 to 6,
            8 to 10,
            12 to 14,
            16 to 16,
            19 to 19,
            22 to 22,
            24 to 24,
            26 to 26,
            28 to 28,
            30 to 31,
            33 to 35,
            37 to 38,
            42 to 42,
            44 to 44
        )

        @BeforeClass
        @JvmStatic
        fun parseTree() {
            val testTree = parser.parseInputStream(File(FILE_PATH).inputStream())
            assertNotNull(testTree)
            functionInfos = functionSplitter.splitIntoFunctions(testTree, FILE_PATH)
        }
    }
}
