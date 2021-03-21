package astminer.parse.python

import astminer.common.model.MethodInfo
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GumTreeMethodSplitterTest {
    private fun parse(filename: String): GumTreePythonNode? =
        GumTreePythonParser().parseInputStream(File(filename).inputStream())

    private fun splitMethods(filename: String): Collection<MethodInfo<GumTreePythonNode>> = parse(filename)?.let {
        GumTreePythonMethodSplitter().splitIntoMethods(it)
    } ?: emptyList()

    private fun createPath(file: String) = "src/test/resources/gumTreeMethodSplitter/$file"

    @Test
    fun methodsCountTest() {
        assertEquals(7, splitMethods(createPath("1.py")).size)
        assertEquals(9, splitMethods(createPath("2.py")).size)
        assertEquals(3, splitMethods(createPath("3.py")).size)
        assertEquals(5, splitMethods(createPath("4.py")).size)
    }

    @Test
    fun funcNamesTest() {
        val realNames = setOf(
            "no_args_func", "with_args_no_typed", "with_typed_args",
            "with_typed_return_no_args", "full_typed",
            "func_dif_args_typed_return", "complex_args_full_typed"
        )
        val methodInfos = splitMethods(createPath("1.py"))
        val parsedNames = methodInfos.map { it.name() }.toSet()
        assertEquals(realNames, parsedNames)
    }

    @Test
    fun methodInfoTest1TypedArgs() {
        val methodInfos = splitMethods(createPath("1.py"))
        val method = methodInfos.firstOrNull { it.name() == "complex_args_full_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("complex_args_full_typed", name())
            assertEquals(null, this.method.returnTypeNode)
            assertEquals(1, methodParameters.size)
            assertEquals(listOf("node"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("JsonNodeType"), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun methodInfoTest2ManyArgs() {
        val methodInfos = splitMethods(createPath("1.py"))
        val method = methodInfos.firstOrNull { it.name() == "func_dif_args_typed_return" }
        assertNotNull(method)
        with(method) {
            assertEquals("func_dif_args_typed_return", name())
            assertEquals("Constant-int", this.method.returnTypeNode?.getTypeLabel())
            assertEquals(6, methodParameters.size)
            assertEquals(listOf("a", "b", "c", "d", "e", "f"), methodParameters.map { it.name() }.toList())
            assertEquals(emptyList(), methodParameters.mapNotNull { it.returnType() }.toList())
        }
    }

    @Test
    fun methodInfoTest3EnclosingClass() {
        val methodInfos = splitMethods(createPath("2.py"))
        val method = methodInfos.firstOrNull { it.name() == "foo_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("foo_typed", name())
            assertEquals("A", enclosingElementName())
            assertEquals(null, this.method.returnTypeNode)
            assertEquals(3, methodParameters.size)
            assertEquals(listOf("self", "x", "y"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf(null, "int", "int"), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun methodInfoTest4EnclosingClass() {
        val methodInfos = splitMethods(createPath("2.py"))
        val method = methodInfos.firstOrNull { it.name() == "bar_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("bar_typed", name())
            assertEquals("C", enclosingElementName())
            assertEquals(null, this.method.returnTypeNode)
            assertEquals(2, methodParameters.size)
            assertEquals(listOf("self", "x"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf(null, "int"), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun methodInfoTest5AsyncDef() {
        val methodInfos = splitMethods(createPath("3.py"))
        val method = methodInfos.firstOrNull { it.name() == "async_schrecklich_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("async_schrecklich_typed", name())
            assertEquals("AsyncFunctionDef", this.method.root.getTypeLabel())
            assertEquals(null, enclosingElementName())
            assertEquals("Constant-int", this.method.returnTypeNode?.getTypeLabel())
            assertEquals(4, methodParameters.size)
            assertEquals(listOf("event", "x", "args", "kwargs"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("str", "int", null, null), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun methodInfoTest6Doc() {
        val methodInfos = splitMethods(createPath("3.py"))
        val method = methodInfos.firstOrNull { it.name() == "async_simple_no_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("async_simple_no_typed", name())
            assertEquals("AsyncFunctionDef", this.method.root.getTypeLabel())
            assertEquals(null, enclosingElementName())
            assertEquals(
                "\n    async doc\n    ",
                this.method.root.getChildOfType("body")
                    ?.getChildOfType("Expr")
                    ?.getChildOfType("Constant-str")
                    ?.getToken()
            )
            assertEquals(4, methodParameters.size)
            assertEquals(
                listOf("gh", "original_issue", "branch", "backport_pr_number"),
                methodParameters.map { it.name() }.toList()
            )
            assertEquals(listOf(null, null, null, null), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun methodInfoTest7InnerFunc() {
        val methodInfos = splitMethods(createPath("4.py"))
        val method = methodInfos.firstOrNull { it.name() == "foo_2" }
        assertNotNull(method)
        with(method) {
            assertEquals("foo_2", name())
            assertEquals("foo_1", method.method.root.parent?.wrappedNode?.parent?.label)
            assertEquals(null, enclosingElementName())
            assertEquals("Constant-NoneType", this.method.returnTypeNode?.getTypeLabel())
            assertEquals(1, methodParameters.size)
            assertEquals(listOf("c"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf(null), methodParameters.map { it.returnType() }.toList())
        }
    }

    @Test
    fun methodInfoTest8InnerFunc() {
        val methodInfos = splitMethods(createPath("4.py"))
        val method = methodInfos.firstOrNull { it.name() == "bar_2" }
        assertNotNull(method)
        with(method) {
            assertEquals("bar_2", name())
            assertEquals("bar_1", method.method.root.parent?.wrappedNode?.parent?.label)
            assertEquals(null, enclosingElementName())
            assertEquals("Constant-int", this.method.returnTypeNode?.getTypeLabel())
            assertEquals(2, methodParameters.size)
            assertEquals(listOf("d", "e"), methodParameters.map { it.name() }.toList())
            assertEquals(listOf("int", "int"), methodParameters.map { it.returnType() }.toList())
        }
    }
}
