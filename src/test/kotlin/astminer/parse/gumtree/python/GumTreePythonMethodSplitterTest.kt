package astminer.parse.gumtree.python

import astminer.common.model.FunctionInfo
import astminer.common.model.MethodInfo
import astminer.parse.gumtree.GumTreeNode
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GumTreePythonMethodSplitterTest {
    private fun parse(filename: String): GumTreeNode? =
        GumTreePythonParser().parseInputStream(File(filename).inputStream())

    private fun splitMethods(filename: String): Collection<FunctionInfo<GumTreeNode>> = parse(filename)?.let {
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
        val parsedNames = methodInfos.map { it.name }.toSet()
        assertEquals(realNames, parsedNames)
    }

    @Test
    fun methodInfoTest1TypedArgs() {
        val methodInfos = splitMethods(createPath("1.py"))
        val method = methodInfos.firstOrNull { it.name == "complex_args_full_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("complex_args_full_typed", name)
            assertEquals(null, returnType)
            assertEquals(1, parameters.size)
            assertEquals(listOf("node"), parameters.map { it.name }.toList())
            assertEquals(listOf("JsonNodeType"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest2ManyArgs() {
        val methodInfos = splitMethods(createPath("1.py"))
        val method = methodInfos.firstOrNull { it.name == "func_dif_args_typed_return" }
        assertNotNull(method)
        with(method) {
            assertEquals("func_dif_args_typed_return", name)
            assertEquals("int", returnType)
            assertEquals(6, parameters.size)
            assertEquals(listOf("a", "b", "c", "d", "e", "f"), parameters.map { it.name }.toList())
            assertEquals(emptyList(), parameters.mapNotNull { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest3EnclosingClass() {
        val methodInfos = splitMethods(createPath("2.py"))
        val method = methodInfos.firstOrNull { it.name == "foo_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("foo_typed", name)
            assertEquals("A", enclosingElement?.name)
            assertEquals(null, returnType)
            assertEquals(3, parameters.size)
            assertEquals(listOf("self", "x", "y"), parameters.map { it.name }.toList())
            assertEquals(listOf(null, "int", "int"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest4EnclosingClass() {
        val methodInfos = splitMethods(createPath("2.py"))
        val method = methodInfos.firstOrNull { it.name == "bar_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("bar_typed", name)
            assertEquals("C", enclosingElement?.name)
            assertEquals(null, returnType)
            assertEquals(2, parameters.size)
            assertEquals(listOf("self", "x"), parameters.map { it.name }.toList())
            assertEquals(listOf(null, "int"), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest5AsyncDef() {
        val methodInfos = splitMethods(createPath("3.py"))
        val method = methodInfos.firstOrNull { it.name == "async_schrecklich_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("async_schrecklich_typed", name)
            assertEquals("AsyncFunctionDef", root.getTypeLabel())
            assertEquals(null, enclosingElement?.name)
            assertEquals("int", returnType)
            assertEquals(4, parameters.size)
            assertEquals(listOf("event", "x", "args", "kwargs"), parameters.map { it.name }.toList())
            assertEquals(listOf("str", "int", null, null), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest6Doc() {
        val methodInfos = splitMethods(createPath("3.py"))
        val method = methodInfos.firstOrNull { it.name == "async_simple_no_typed" }
        assertNotNull(method)
        with(method) {
            assertEquals("async_simple_no_typed", name)
            assertEquals("AsyncFunctionDef", root.getTypeLabel())
            assertEquals(null, enclosingElement?.name)
            assertEquals(
                "\n    async doc\n    ",
                root.getChildOfType("body")
                    ?.getChildOfType("Expr")
                    ?.getChildOfType("Constant-str")
                    ?.getToken()
            )
            assertEquals(4, parameters.size)
            assertEquals(
                listOf("gh", "original_issue", "branch", "backport_pr_number"),
                parameters.map { it.name }.toList()
            )
            assertEquals(listOf(null, null, null, null), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest7InnerFunc() {
        val methodInfos = splitMethods(createPath("4.py"))
        val method = methodInfos.firstOrNull { it.name == "foo_2" }
        assertNotNull(method)
        with(method) {
            assertEquals("foo_2", name)
            assertEquals("foo_1", method.root.parent?.wrappedNode?.parent?.label)
            assertEquals(null, enclosingElement?.name)
            assertEquals("None", returnType)
            assertEquals(1, parameters.size)
            assertEquals(listOf("c"), parameters.map { it.name }.toList())
            assertEquals(listOf(null), parameters.map { it.type }.toList())
        }
    }

    @Test
    fun methodInfoTest8InnerFunc() {
        val methodInfos = splitMethods(createPath("4.py"))
        val method = methodInfos.firstOrNull { it.name == "bar_2" }
        assertNotNull(method)
        with(method) {
            assertEquals("bar_2", name)
            assertEquals("bar_1", method.root.parent?.wrappedNode?.parent?.label)
            assertEquals(null, enclosingElement?.name)
            assertEquals("int", returnType)
            assertEquals(2, parameters.size)
            assertEquals(listOf("d", "e"), parameters.map { it.name }.toList())
            assertEquals(listOf("int", "int"), parameters.map { it.type }.toList())
        }
    }
}
