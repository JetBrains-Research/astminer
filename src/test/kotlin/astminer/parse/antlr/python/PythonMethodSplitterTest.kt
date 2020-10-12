package astminer.parse.antlr.python

import astminer.common.model.MethodInfo
import astminer.parse.antlr.SimpleNode
import org.junit.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PythonMethodSplitterTest {
    companion object {
        const val N_FUNCTIONS = 6
        val methodSplitter = PythonMethodSplitter()
        val parser = PythonParser()
    }

    var methodInfos: Collection<MethodInfo<SimpleNode>> = listOf()

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
        val methodClass = methodInfos.find { it.name() == "funWithNoClass"  }
        assertNotNull(methodClass)
        assertNull(methodClass.enclosingElement.root)
    }

    @Test
    fun testFunctionInClass() {
        val methodClass = methodInfos.find { it.name() == "funInClass1"  }
        assertNotNull(methodClass)
        assertEquals( "Class1", methodClass.enclosingElementName())
    }

    @Test
    fun testFunctionInNestedClass() {
        val methodClass = methodInfos.find { it.name() == "funInClass2"  }
        assertNotNull(methodClass)
        assertEquals( "Class2", methodClass.enclosingElementName())
    }

    @Test
    fun testNoParameters() {
        val methodNoParameters = methodInfos.find { it.name() == "functionWithNoParameters"  }
        assertNotNull(methodNoParameters)
        assertEquals(0, methodNoParameters.methodParameters.size)
    }

    @Test
    fun testOneParameter() {
        val methodOneParameter = methodInfos.find { it.name() == "functionWithOneParameter"  }
        assertNotNull(methodOneParameter)
        assertEquals(1, methodOneParameter.methodParameters.size)
        val parameter = methodOneParameter.methodParameters[0]
        assertEquals("p1", parameter.name())
    }

    @Test
    fun testThreeParameters() {
        val methodThreeParameters = methodInfos.find { it.name() == "functionWithThreeParameters"  }
        assertNotNull(methodThreeParameters)
        assertEquals(3, methodThreeParameters.methodParameters.size)
        for (i in 0 until 3) {
            val parameter = methodThreeParameters.methodParameters[i]
            assertEquals("p${i + 1}", parameter.name())
        }
    }
}