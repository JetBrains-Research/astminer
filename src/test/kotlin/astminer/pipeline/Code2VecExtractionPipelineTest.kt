package astminer.pipeline

import astminer.cli.util.verifyPathContextExtraction
import astminer.config.Code2VecPathStorageConfig
import astminer.config.FilePipelineConfig
import astminer.config.ParserConfig
import astminer.problem.FilePathExtractor
import org.junit.Test
import java.io.File

internal class Code2VecExtractionPipelineTest {
    private val testDataDir = File("src/test/resources")

    @Test
    fun testDefaultExtraction() {
        val extractedDataDir = createTempDir("extractedData")
        val languages = listOf("java", "py")

        val config = FilePipelineConfig(
            testDataDir.path,
            extractedDataDir.path,
            ParserConfig(
                "gumtree",
                languages
            ),
            emptyList(),
            FilePathExtractor,
            Code2VecPathStorageConfig(
                maxPathLength = 8,
                maxPathWidth = 3
            )
        )

        val pipeline = getFilePipeline(config)

        pipeline.run()

        verifyPathContextExtraction(extractedDataDir, languages, false)
    }
}