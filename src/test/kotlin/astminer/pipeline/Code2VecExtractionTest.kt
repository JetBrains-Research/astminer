package astminer.pipeline

import astminer.config.Code2VecPathStorageConfig
import astminer.config.FilePathExtractorConfig
import astminer.config.FilePipelineConfig
import astminer.config.ParserConfig
import astminer.pipeline.util.verifyPathContextExtraction
import org.junit.Test
import java.io.File
import java.nio.file.Files

internal class Code2VecExtractionTest {
    private val testDataDir = File("src/test/resources")

    // TODO: this test should probably be moved to Code2VecPathStorage
    @Test
    fun `test code2vec path extraction from files generates correct folders and files`() {
        val extractedDataDir = Files.createTempDirectory("extractedData")

        val languages = listOf("java", "py")

        val config = FilePipelineConfig(
            inputDir = testDataDir.path,
            outputDir = extractedDataDir.toAbsolutePath().toString(),
            parser = ParserConfig("antlr", languages),
            problem = FilePathExtractorConfig(),
            storage = Code2VecPathStorageConfig(8, 3)
        )
        Pipeline(config).run()

        verifyPathContextExtraction(extractedDataDir.toFile(), languages, false)
    }
}
