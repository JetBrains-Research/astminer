package astminer.pipeline

import astminer.common.DummyNode
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

internal class PipelineTest {
    companion object {
        val extensionsToNodeNames = mapOf("a" to "A", "b" to "B")
    }

    lateinit var storageCreator: DummyStorageCreator

    @Before
    fun init() {
        storageCreator = DummyStorageCreator()
    }

    @Test
    fun `test pipeline saves all entities if none are filtered and no node types are excluded`() {
        Pipeline(
            frontend = DummyPipelineFrontend(extensionsToNodeNames),
            problem = DummyLabelExtractor(),
            storageCreator = storageCreator
        ).run()

        val expectedResults = mapOf(
            "a" to setOf("label A"),
            "b" to setOf("label B")
        )
        assertEquals(expectedResults, storageCreator.results)
    }

    @Test
    fun `test pipeline saves A if B is filtered out by a DummyFilter`() {
        Pipeline(
            frontend = DummyPipelineFrontend(extensionsToNodeNames),
            filters = listOf(DummyFilter("B")),
            problem = DummyLabelExtractor(),
            storageCreator = storageCreator
        ).run()

        val expectedResults = mapOf(
            "a" to setOf("label A"),
            "b" to setOf()
        )
        assertEquals(expectedResults, storageCreator.results)
    }

    @Test
    fun `test pipeline saves A if B is filtered out by a DummyLabelExtractor`() {
        Pipeline(
            frontend = DummyPipelineFrontend(extensionsToNodeNames),
            filters = listOf(DummyFilter()),
            problem = DummyLabelExtractor("B"),
            storageCreator = storageCreator
        ).run()

        val expectedResults = mapOf(
            "a" to setOf("label A"),
            "b" to setOf()
        )
        assertEquals(expectedResults, storageCreator.results)
    }

    @Test
    fun `test pipeline saves nothing if 'label A' is filtered by a filter and 'label B' is filtered by a problem`() {
        Pipeline(
            frontend = DummyPipelineFrontend(extensionsToNodeNames),
            filters = listOf(DummyFilter("A")),
            problem = DummyLabelExtractor("B"),
            storageCreator = storageCreator
        ).run()

        val expectedResults = mapOf(
            "a" to setOf<String>(),
            "b" to setOf<String>()
        )
        assertEquals(expectedResults, storageCreator.results)
    }

    @Test
    fun `test pipeline should not remove any nodes from the tree by default`() {
        val node = DummyNode("Root", mutableListOf(DummyNode("Child")))

        Pipeline(
            frontend = SimplePipelineFrontend(listOf(node)),
            problem = BambooLabelExtractor(),
            storageCreator = storageCreator
        ).run()

        val expectedResults = mapOf(
            "" to setOf("Root<Child<")
        )
        assertEquals(expectedResults, storageCreator.results)
    }

    @Test
    fun `test pipeline removes nodes from trees`() {
        val node = DummyNode("Root", mutableListOf(DummyNode("Child")))

        Pipeline(
            frontend = SimplePipelineFrontend(listOf(node)),
            problem = BambooLabelExtractor(),
            excludedNodeTypes = listOf("Child"),
            storageCreator = storageCreator
        ).run()

        val expectedResults = mapOf(
            "" to setOf("Root<")
        )
        assertEquals(expectedResults, storageCreator.results)
    }
}