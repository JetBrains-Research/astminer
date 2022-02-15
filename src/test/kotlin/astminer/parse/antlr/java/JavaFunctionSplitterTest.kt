package astminer.parse.antlr.java

import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import java.io.File
import kotlin.test.*

class JavaFunctionSplitterTest {

    var functionInfos: Collection<FunctionInfo<AntlrNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree = parser.parseInputStream(File(FILE_PATH).inputStream())
        assertNotNull(testTree)
        functionInfos = functionSplitter.splitIntoFunctions(testTree, FILE_PATH)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_FUNCTIONS, functionInfos.size, "Test file contains $N_FUNCTIONS methods")
    }

    @Test
    fun testReturnVoid() {
        val methodVoid = functionInfos.find { it.name == "functionReturningVoid" }
        assertNotNull(methodVoid)
        assertEquals("void", methodVoid.returnType)
        assertTrue(methodVoid.isBlank())
    }

    @Test
    fun testReturnInt() {
        val methodInt = functionInfos.find { it.name == "functionReturningInt" }
        assertNotNull(methodInt)
        assertEquals("int", methodInt.returnType)
        assertFalse(methodInt.isBlank())
    }

    @Test
    fun testReturnStrings() {
        val methodStrings = functionInfos.find { it.name == "functionReturningStrings" }
        assertNotNull(methodStrings)
        assertEquals("String[]", methodStrings.returnType)
        assertFalse(methodStrings.isBlank())
    }

    @Test
    fun testReturnClass() {
        val methodClass = functionInfos.find { it.name == "functionReturningClass" }
        assertNotNull(methodClass)
        assertEquals("Class1", methodClass.returnType)
        assertFalse(methodClass.isBlank())
    }

    @Test
    fun testFunctionInClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass1" }
        assertNotNull(methodClass)
        assertEquals("Class1", methodClass.enclosingElement?.name)
        assertTrue(methodClass.isBlank())
    }

    @Test
    fun testFunctionInNestedClass() {
        val methodClass = functionInfos.find { it.name == "functionInClass2" }
        assertNotNull(methodClass)
        assertEquals("Class2", methodClass.enclosingElement?.name)
        assertTrue(methodClass.isBlank())
    }

    @Test
    fun testNoParameters() {
        val methodNoParameters = functionInfos.find { it.name == "functionWithNoParameters" }
        assertNotNull(methodNoParameters)
        assertEquals(0, methodNoParameters.parameters?.size)
        assertTrue(methodNoParameters.isBlank())
    }

    @Test
    fun testOneParameter() {
        val methodOneParameter = functionInfos.find { it.name == "functionWithOneParameter" }
        assertNotNull(methodOneParameter)
        assertEquals(1, methodOneParameter.parameters?.size)
        val parameter = methodOneParameter.parameters?.get(0)
        assertEquals("p1", parameter?.name)
        assertEquals("int", parameter?.type)
        assertTrue(methodOneParameter.isBlank())
    }

    @Test
    fun testThreeParameters() {
        val methodThreeParameters = functionInfos.find { it.name == "functionWithThreeParameters" }
        assertNotNull(methodThreeParameters)
        assertEquals(3, methodThreeParameters.parameters?.size)
        val methodTypes = listOf("Class", "String[][]", "int[]")
        for (i in 0 until 3) {
            val parameter = methodThreeParameters.parameters?.get(i)
            assertEquals("p${i + 1}", parameter?.name)
            assertEquals(methodTypes[i], parameter?.type)
        }
        assertTrue(methodThreeParameters.isBlank())
    }

    @Test
    fun testWeirdArrayParameter() {
        val methodWeirdArrayParameter = functionInfos.find { it.name == "functionWithStrangeArrayParameter" }
        assertNotNull(methodWeirdArrayParameter)
        assertEquals(1, methodWeirdArrayParameter.parameters?.size)
        val weirdParameter = methodWeirdArrayParameter.parameters?.get(0)
        assertEquals(weirdParameter?.name, "arr[]")
        assertEquals(weirdParameter?.type, "int")
        assertTrue(methodWeirdArrayParameter.isBlank())
    }

    @Test
    fun testOneModifierFunction() {
        val methodWithOneModifier = functionInfos.find { it.name == "abstractFunctionReturningInt" }
        assertNotNull(methodWithOneModifier)
        val modifiers = methodWithOneModifier.modifiers
        assertNotNull(modifiers)
        assertEquals("abstract", modifiers.first())
        assertTrue(methodWithOneModifier.isBlank())
    }

    @Test
    fun testMultipleModifiersFunction() {
        val methodWithMultipleModifiers = functionInfos.find { it.name == "staticFunctionReturningString" }
        assertNotNull(methodWithMultipleModifiers)
        val modifiers = methodWithMultipleModifiers.modifiers
        assertNotNull(modifiers)
        assertEquals(setOf("static", "public", "final"), modifiers.toSet())
        assertFalse(methodWithMultipleModifiers.isBlank())
    }

    @Test
    fun testOneAnnotationFunction() {
        val methodWithOneAnnotation = functionInfos.find { it.name == "deprecatedFunction" }
        assertNotNull(methodWithOneAnnotation)
        val annotations = methodWithOneAnnotation.annotations
        assertNotNull(annotations)
        assertEquals(setOf("Deprecated"), annotations.toSet())
        assertTrue(methodWithOneAnnotation.isBlank())
    }

    @Test
    fun testMultipleAnnotationsFunction() {
        val methodWithOneAnnotation = functionInfos.find { it.name == "functionWithAnnotations" }
        assertNotNull(methodWithOneAnnotation)
        val annotations = methodWithOneAnnotation.annotations
        assertNotNull(annotations)
        assertEquals(setOf("Deprecated", "SuppressWarnings"), annotations.toSet())
        assertTrue(methodWithOneAnnotation.isBlank())
    }

    @Test
    fun testModifiersAndAnnotation() {
        val methodWithModifierAndAnnotation = functionInfos.find { it.name == "functionWithModifiersAndAnnotations" }
        assertNotNull(methodWithModifierAndAnnotation)
        val modifiers = methodWithModifierAndAnnotation.modifiers
        assertNotNull(modifiers)
        val annotations = methodWithModifierAndAnnotation.annotations
        assertNotNull(annotations)
        assertEquals(setOf("public", "static"), modifiers.toSet())
        assertEquals(setOf("Deprecated"), annotations.toSet())
        assertTrue(methodWithModifierAndAnnotation.isBlank())
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
        const val FILE_PATH = "src/test/resources/methodSplitting/testMethodSplitting.java"
        const val N_FUNCTIONS = 15
        val functionSplitter = JavaFunctionSplitter()
        val parser = JavaParser()
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
    }
}
