package astminer.problem

import astminer.common.DummyNode
import astminer.common.getTechnicalToken
import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FunctionNameProblemTest {
    companion object {
        private const val PATH = "random/folder/file.txt"
        private const val FUNCTION_NAME = "method"
    }

    lateinit var functionRoot: Node

    private val functionInfo: FunctionInfo<Node>
        get() = object : FunctionInfo<Node> {
            override val nameNode = functionRoot
            override val filePath = PATH
            override val root = functionRoot
        }

    @Before
    fun init() {
        val leafNodeWithRecursiveCall = DummyNode(FUNCTION_NAME)
        val emptyIntermediateNode = DummyNode("", mutableListOf(leafNodeWithRecursiveCall))
        functionRoot = DummyNode(FUNCTION_NAME, mutableListOf(emptyIntermediateNode))
    }

    @Test
    fun `test FunctionNameProblem extracts correct method name`() {
        val labeledResult = FunctionNameProblem.process(functionInfo)
        assertEquals(LabeledResult(functionRoot, FUNCTION_NAME, PATH), labeledResult)
    }

    @Test
    fun `test FunctionNameProblem hides function name node token with METHOD_NAME`() {
        FunctionNameProblem.process(functionInfo)
        assertEquals("METHOD_NAME", functionInfo.nameNode?.getTechnicalToken())
    }

    @Test
    fun `test FunctionNameProblem hides function root token with METHOD_NAME if it is the name node`() {
        FunctionNameProblem.process(functionInfo)
        assertEquals("METHOD_NAME", functionInfo.root.getTechnicalToken())
    }

    @Test
    fun `test function name problem should hide recursive call tokens with SELF`() {
        FunctionNameProblem.process(functionInfo)
        val recursiveCallNode = functionInfo.root.children.firstOrNull()?.children?.firstOrNull()
        assertEquals("SELF", recursiveCallNode?.getTechnicalToken())
    }
}