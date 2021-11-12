package astminer.labelextractor

import astminer.common.DummyNode
import astminer.common.model.EnclosingElement
import astminer.common.model.FunctionInfo
import astminer.common.model.LabeledResult
import astminer.common.model.Node
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FunctionNameLabelExtractorTest {

    lateinit var functionRoot: Node

    private val functionInfo: FunctionInfo<Node>
        get() = object : FunctionInfo<Node> {
            override val nameNode = functionRoot
            override val filePath = PATH
            override val root = functionRoot
            override val enclosingElement: EnclosingElement<Node>? = null
        }

    @Before
    fun init() {
        val leafNodeWithRecursiveCall = DummyNode(FUNCTION_NAME)
        val emptyIntermediateNode = DummyNode("", mutableListOf(leafNodeWithRecursiveCall))
        functionRoot = DummyNode(FUNCTION_NAME, mutableListOf(emptyIntermediateNode))
    }

    @Test
    fun `test FunctionNameProblem extracts correct method name`() {
        val labeledResult = FunctionNameLabelExtractor.process(functionInfo)
        assertEquals(LabeledResult(functionRoot, FUNCTION_NAME, PATH), labeledResult)
    }

    @Test
    fun `test FunctionNameProblem hides function name node token with METHOD_NAME`() {
        FunctionNameLabelExtractor.process(functionInfo)
        assertEquals("METHOD_NAME", functionInfo.nameNode?.token?.final())
    }

    @Test
    fun `test FunctionNameProblem hides function root token with METHOD_NAME if it is the name node`() {
        FunctionNameLabelExtractor.process(functionInfo)
        assertEquals("METHOD_NAME", functionInfo.root.token.final())
    }

    @Test
    fun `test function name problem should not hide tokens with same content`() {
        val labeledResult = FunctionNameLabelExtractor.process(functionInfo)
        val recursiveCallNode = functionInfo.root.children.firstOrNull()?.children?.firstOrNull()
        assertEquals(labeledResult?.label, recursiveCallNode?.token?.final())
    }

    companion object {
        private const val PATH = "random/folder/file.txt"
        private const val FUNCTION_NAME = "method"
    }
}
