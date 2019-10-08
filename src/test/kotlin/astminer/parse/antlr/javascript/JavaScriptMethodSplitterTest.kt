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
        const val N_METHODS = 43

        const val N_ANONYMOUS_WITHOUT_ARGS = 3
        const val N_ANONYMOUS_WITH_ARG = 7
        const val N_ANONYMOUS_WITH_ARGS = 3

        const val N_ANONYMOUS_CLASS_ENCLOSING = 0
        const val N_ANONYMOUS_METHOD_ENCLOSING = 0
        const val N_ANONYMOUS_VAR_ENCLOSING = 7
        const val N_ANONYMOUS_EXPR_ENCLOSING = 4
        const val N_ANONYMOUS_RETURN_ENCLOSING = 2
        const val N_ANONYMOUS_FUN_ENCLOSING = 0
        const val N_ANONYMOUS_NULL_ENCLOSING = 0

        val methodSplitter = JavaScriptMethodSplitter()
        val parser = JavaScriptParser()
    }

    var methodInfos: Collection<MethodInfo<SimpleNode>> = listOf()

    @BeforeTest
    fun parseTree() {
        val testTree =  parser.parse(File("testData/methodSplitting/javascript/testMethodSplitting.js").inputStream())
        assertNotNull(testTree)
        methodInfos = methodSplitter.splitIntoMethods(testTree)

        methodInfos.map {
            print("${it.name()} ${it.enclosingElement.root?.getTypeLabel()} ${it.enclosingElementName()} { ")
            it.methodParameters.map { print("${it.name()} ") }
            println("}")
        }
        testTree.prettyPrint()
    }

    @Test
    fun testValidSplitting() {
        assertEquals(N_METHODS, methodInfos.size, "Test file contains $N_METHODS methods")
    }

    @Test
    fun testNotAnonymousFunArgs() {
        fun getNumberOfArgs(name: String) : List<String> {
            return when {
                name.contains("WithArgs") -> listOf("a", "b", "c").sorted()
                name.contains("WithArg") -> listOf("a")
                else -> emptyList()
            }
        }
        methodInfos.filter { it.name() != null }.forEach { m ->
            assertEquals(getNumberOfArgs(m.name()!!), m.methodParameters.mapNotNull { it.name() }.sorted())
        }
    }

    @Test
    fun testAnonymousFunArgs() {
        val methodsByArgs = methodInfos.filter { it.name() == null }.groupBy { it.methodParameters.size }
        assertEquals(3, methodsByArgs.size)
        assertEquals(N_ANONYMOUS_WITHOUT_ARGS, methodsByArgs[0]?.size)
        assertEquals(N_ANONYMOUS_WITH_ARG, methodsByArgs[1]?.size)
        assertEquals(N_ANONYMOUS_WITH_ARGS, methodsByArgs[3]?.size)
    }

    @Test
    fun testNotAnonymousFunEnclosingElement() {
        fun getEnclosingElementType(name: String) : String? {
            return when {
                name.contains("funFun") -> "functionDeclaration"
                name.contains("class") -> "classDeclaration"
                name.contains("var") -> "variableDeclaration"
                name.contains("method") -> "methodDefinition"
                name.contains("expr") -> "expressionStatement"
                name.contains("return") -> "returnStatement"
                else -> null
            }
        }
        methodInfos.filter { it.name() != null && it.enclosingElement.root == null }.forEach {
            assertEquals(getEnclosingElementType(it.name()!!), null)
        }

        methodInfos.filter { it.name() != null && it.enclosingElement.root != null}.forEach {
            assertTrue(decompressTypeLabel(it.enclosingElement.root!!.getTypeLabel()).contains(getEnclosingElementType(it.name()!!)))
        }
    }

    @Test
    fun testAnonymousFunEnclosingElement() {
        val nByEnclosingTypes = mapOf("functionDeclaration" to N_ANONYMOUS_FUN_ENCLOSING,
               "classDeclaration" to N_ANONYMOUS_CLASS_ENCLOSING, "variableDeclaration" to N_ANONYMOUS_VAR_ENCLOSING,
                "methodDefinition" to N_ANONYMOUS_METHOD_ENCLOSING, "expressionStatement" to N_ANONYMOUS_EXPR_ENCLOSING,
                "returnStatement" to N_ANONYMOUS_RETURN_ENCLOSING, null to N_ANONYMOUS_NULL_ENCLOSING)

        fun String.getEnclosingElementType(): String? {
            for (type in nByEnclosingTypes.keys.filterNotNull()) {
                if (contains(type)) {
                    return type
                }
            }
            return null
        }

        val methodsByEnclosing = methodInfos.filter { it.name() == null }.groupingBy {
            it.enclosingElement.root?.getTypeLabel()?.getEnclosingElementType()
        }.eachCount()
        nByEnclosingTypes.keys.forEach {
            assertEquals(nByEnclosingTypes[it], methodsByEnclosing.getOrDefault(it, 0))
        }
    }
}