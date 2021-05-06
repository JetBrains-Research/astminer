package astminer.pipeline

import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import org.junit.Test
import kotlin.test.assertEquals

internal class CompositePipelineFrontendTest {
    class DummyCompositeFrontend(inputDirectoryPath: String, parserType: String, extensions: List<String>) :
        CompositePipelineFrontend<Unit>(inputDirectoryPath, parserType, extensions) {

        override fun LanguageHandler<out Node>.getEntities(): Sequence<Unit> = sequenceOf(Unit)
    }

    @Test
    fun `test should skip language if it is not supported`() {
        val tempDir = createTempDirectoryWithEmptyFiles(mapOf("py" to 5))
        val frontend = DummyCompositeFrontend(tempDir.path, "antlr", listOf("py", "unsupported_language"))
        val entitiesCounts = getExtractedEntitiesCounts(frontend.getEntities())

        assertEquals(mapOf("py" to 5, "unsupported_language" to 0), entitiesCounts)
    }
}
