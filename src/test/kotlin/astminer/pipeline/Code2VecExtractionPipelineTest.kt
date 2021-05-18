package astminer.pipeline

import astminer.cli.util.verifyPathContextExtraction
import astminer.config.Code2VecPathStorageFactoryConfig
import astminer.config.FilePathExtractorConfig
import astminer.config.FilePipelineConfig
import astminer.config.ParserConfig
import org.junit.Test
import java.io.File
import java.nio.file.Files.createTempDirectory

internal class Code2VecExtractionPipelineTest {
    private val testDataDir = File("src/test/resources")

    @Test
    fun testDefaultExtraction() {
        val extractedDataDir = createTempDirectory("extractedData").toFile()

        val languages = listOf("java", "python")

        val config = FilePipelineConfig(
            inputDir = testDataDir.path,
            outputDir = extractedDataDir.path,
            parserConfig = ParserConfig(
                "gumtree",
                languages
            ),
            problemConfig = FilePathExtractorConfig(),
            storageFactoryConfig = Code2VecPathStorageFactoryConfig(
                maxPathLength = 8,
                maxPathWidth = 3
            )
        )

        val pipeline = getFilePipeline(config)

        pipeline.run()

        verifyPathContextExtraction(extractedDataDir, languages, false)
    }
}