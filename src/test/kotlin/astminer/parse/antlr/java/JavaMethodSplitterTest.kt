package astminer.parse.antlr.java

import astminer.common.model.MethodInfo
import astminer.parse.antlr.SimpleNode
import org.junit.Test
import kotlin.test.assertEquals
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

class JavaMethodSplitterTest {
    companion object {
        const val N_FUNCTIONS = 9
        val methodSplitter = JavaMethodSplitter()
        val parser = JavaParser()
    }

    var methodInfos: Collection<MethodInfo<SimpleNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree =  parser.parseInputStream(File("src/test/resources/methodSplitting/testMethodSplitting.java").inputStream())
        assertNotNull(testTree)
        methodInfos = methodSplitter.splitIntoMethods(testTree)
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_FUNCTIONS, methodInfos.size, "Test file contains $N_FUNCTIONS methods")
    }

    @Test
    fun testReturnVoid() {
        val methodVoid = methodInfos.find { it.name() == "functionReturningVoid"  }
        assertNotNull(methodVoid)
        assertEquals( "void", methodVoid.returnType())
    }

    @Test
    fun testReturnInt() {
        val methodInt = methodInfos.find { it.name() == "functionReturningInt"  }
        assertNotNull(methodInt)
        assertEquals( "int", methodInt.returnType())
    }

    @Test
    fun testReturnStrings() {
        val methodStrings = methodInfos.find { it.name() == "functionReturningStrings"  }
        assertNotNull(methodStrings)
        assertEquals( "String[]", methodStrings.returnType())
    }

    @Test
    fun testReturnClass() {
        val methodClass = methodInfos.find { it.name() == "functionReturningClass"  }
        assertNotNull(methodClass)
        assertEquals( "Class1", methodClass.returnType())
    }

    @Test
    fun testFunctionInClass() {
        val methodClass = methodInfos.find { it.name() == "functionInClass1"  }
        assertNotNull(methodClass)
        assertEquals( "Class1", methodClass.enclosingElementName())
    }

    @Test
    fun testFunctionInNestedClass() {
        val methodClass = methodInfos.find { it.name() == "functionInClass2"  }
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
        assertEquals("int", parameter.returnType())
    }

    @Test
    fun testThreeParameters() {
        val methodThreeParameters = methodInfos.find { it.name() == "functionWithThreeParameters"  }
        assertNotNull(methodThreeParameters)
        assertEquals(3, methodThreeParameters.methodParameters.size)
        val methodTypes = listOf("Class", "String[][]", "int[]")
        for (i in 0 until 3) {
            val parameter = methodThreeParameters.methodParameters[i]
            assertEquals("p${i + 1}", parameter.name())
            assertEquals(methodTypes[i], parameter.returnType())
        }
    }
}