package astminer.parse.antlr.python

import astminer.common.model.EnclosingElementType
import astminer.common.model.FunctionInfo
import astminer.common.model.MethodInfo
import astminer.parse.antlr.AntlrNode
import org.junit.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PythonMethodSplitterTest {
    companion object {
        const val N_FUNCTIONS = 17
        val methodSplitter = PythonMethodSplitter()
        val parser = PythonParser()
    }

    var methodInfos: Collection<FunctionInfo<AntlrNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree =  parser.parseInputStream(File("src/test/resources/methodSplitting/testMethodSplitting.py").inputStream())
        assertNotNull(testTree)
        methodInfos = methodSplitter.splitIntoMethods(testTree)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_FUNCTIONS, methodInfos.size, "Test file contains $N_FUNCTIONS methods")
    }

    @Test
    fun testFunctionNotInClass() {
        val methodClass = methodInfos.find { it.name == "fun_with_no_class"  }
        assertNotNull(methodClass)
        assertNull(methodClass.enclosingElement)
    }

    @Test
    fun testFunctionInClass() {
        val methodClass = methodInfos.find { it.name == "fun_in_class1"  }
        assertNotNull(methodClass)
        assertEquals(EnclosingElementType.Class, methodClass.enclosingElement?.type)
        assertEquals( "Class1", methodClass.enclosingElement?.name)
    }

    @Test
    fun testFunctionInNestedClass() {
        val methodClass = methodInfos.find { it.name == "fun_in_class2"  }
        assertNotNull(methodClass)
        assertEquals(EnclosingElementType.Class, methodClass.enclosingElement?.type)
        assertEquals( "Class2", methodClass.enclosingElement?.name)
    }

    @Test
    fun testNoParameters() {
        val methodNoParameters = methodInfos.find { it.name == "function_with_no_parameters"  }
        assertNotNull(methodNoParameters)
        assertEquals(0, methodNoParameters.parameters.size)
    }

    @Test
    fun testOneParameter() {
        val methodOneParameter = methodInfos.find { it.name == "function_with_one_parameter"  }
        assertNotNull(methodOneParameter)
        assertEquals(1, methodOneParameter.parameters.size)
        val parameter = methodOneParameter.parameters[0]
        assertEquals("p1", parameter.name)
    }

    @Test
    fun testOneTypedParameter() {
        val methodOneTypedParameter = methodInfos.find { it.name == "function_with_one_typed_parameter" }
        assertNotNull(methodOneTypedParameter)
        assertEquals(1, methodOneTypedParameter.parameters.size)
        val parameter = methodOneTypedParameter.parameters[0]
        assertEquals("p1", parameter.name)
        assertEquals("int", parameter.type)
    }

    @Test
    fun functionWithComplexParameter() {
        val methodOneTypedParameter = methodInfos.find { it.name == "function_with_complex_parameter" }
        assertNotNull(methodOneTypedParameter)
        assertEquals(1, methodOneTypedParameter.parameters.size)
        val parameter = methodOneTypedParameter.parameters[0]
        assertEquals("p1", parameter.name)
        assertEquals("List[int]", parameter.type)
    }

    @Test
    fun testThreeParameters() {
        val methodThreeParameters = methodInfos.find { it.name == "function_with_three_parameters"  }
        assertNotNull(methodThreeParameters)
        assertEquals(3, methodThreeParameters.parameters.size)
        val parameters = methodThreeParameters.parameters
        assertEquals("p1", parameters[0].name)

        assertEquals("p2", parameters[1].name)

        assertEquals("p3", parameters[2].name)
        assertEquals("int", parameters[2].type)
    }

    @Test
    fun testParameterInClass() {
        val methodOneParameter = methodInfos.find { it.name == "fun_with_parameter_in_class"  }
        assertNotNull(methodOneParameter)
        assertEquals(2, methodOneParameter.parameters.size)
        val parameter = methodOneParameter.parameters[1]
        assertEquals("p1", parameter.name)
    }

    @Test
    fun testTypedParameterInClass() {
        val methodOneTypedParameter = methodInfos.find { it.name == "fun_with_typed_parameter_in_class" }
        assertNotNull(methodOneTypedParameter)
        assertEquals(2, methodOneTypedParameter.parameters.size)
        val parameter = methodOneTypedParameter.parameters[1]
        assertEquals("p1", parameter.name)
        assertEquals("int", parameter.type)
    }

    @Test
    fun testEnclosingFunction() {
        val functionInsideFunction = methodInfos.find { it.name == "function_inside_function" }
        assertNotNull(functionInsideFunction)
        val enclosingElement = functionInsideFunction.enclosingElement

        assertNotNull(enclosingElement)
        assertEquals("function_containing_function", enclosingElement.name)
        assertEquals(EnclosingElementType.Function, enclosingElement.type)
    }

    @Test
    fun testEnclosingMethod() {
        val functionInsideMethod =  methodInfos.find { it.name == "function_inside_method" }
        assertNotNull(functionInsideMethod)
        val enclosingElement = functionInsideMethod.enclosingElement

        assertNotNull(enclosingElement)
        assertEquals("some_method", enclosingElement.name)
        assertEquals(EnclosingElementType.Method, enclosingElement.type)
    }

    @Test
    fun testEnclosingFunctionInsideMethod() {
        val funInsideFunInsideMethod =  methodInfos.find { it.name == "fun_inside_fun_inside_method" }
        assertNotNull(funInsideFunInsideMethod)
        val enclosingElement = funInsideFunInsideMethod.enclosingElement

        assertNotNull(enclosingElement)
        assertEquals("second_function_inside_method", enclosingElement.name)
        assertEquals(EnclosingElementType.Function, enclosingElement.type)
    }
}