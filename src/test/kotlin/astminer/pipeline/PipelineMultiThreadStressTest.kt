package astminer.pipeline

import astminer.config.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.test.assertEquals

class PipelineMultiThreadStressTest {
    @Test
    fun jsonStorageTest() {
        val outputPath = tempOutputDir.resolve("json").path
        val config = PipelineConfig(
            inputDir = tempInputDir.path,
            outputDir = outputPath,
            parser = ParserConfig(
                name = ParserType.Antlr,
                languages = listOf(FileExtension.Java)
            ),
            filters = listOf(),
            labelExtractor = FunctionNameExtractorConfig(),
            storage = JsonAstStorageConfig(),
            numOfThreads = 8
        )
        Pipeline(config).run()
        val expectedNumOfAst = numOfFiles * numOfMethods
        val actualNumOfAst = countLines("$outputPath/java/data/asts.jsonl")
        assertEquals(expected = expectedNumOfAst.toLong(), actual = actualNumOfAst)
    }

    @Test
    fun code2vecStorageTest() {
        val outputPath = tempOutputDir.resolve("code2vec").path
        val config = PipelineConfig(
            inputDir = tempInputDir.path,
            outputDir = outputPath,
            parser = ParserConfig(
                name = ParserType.Antlr,
                languages = listOf(FileExtension.Java)
            ),
            filters = listOf(),
            labelExtractor = FunctionNameExtractorConfig(),
            storage = Code2VecPathStorageConfig(
                maxPaths = null,
                maxTokens = null,
                maxPathContextsPerEntity = null,
                maxPathLength = 1000,
                maxPathWidth = 1000
            ),
            numOfThreads = 8
        )
        Pipeline(config).run()
        val expectedNumOfPathContexts = numOfFiles * numOfMethods
        val actualNumOfPathContexts = countLines("$outputPath/java/data/path_contexts.c2s")
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfPathContexts)
    }

    @Test
    fun code2seqStorageTest() {
        val outputPath = tempOutputDir.resolve("code2seq").path
        val config = PipelineConfig(
            inputDir = tempInputDir.path,
            outputDir = outputPath,
            parser = ParserConfig(
                name = ParserType.Antlr,
                languages = listOf(FileExtension.Java)
            ),
            filters = listOf(),
            labelExtractor = FunctionNameExtractorConfig(),
            storage = Code2SeqPathStorageConfig(
                maxPathContextsPerEntity = null,
                maxPathLength = 1000,
                maxPathWidth = 1000
            ),
            numOfThreads = 8
        )
        Pipeline(config).run()
        val expectedNumOfPathContexts = numOfFiles * numOfMethods
        val actualNumOfPathContexts = countLines("$outputPath/java/data/path_contexts.c2s")
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfPathContexts)
    }

    private fun countLines(filePath: String): Long {
        val reader = BufferedReader(FileReader(filePath))
        var numOfLines = 0L
        while (reader.readLine() != null) { numOfLines++ }
        return numOfLines
    }

    companion object {
        private const val numOfFiles = 3000
        private const val numOfMethods = 100
        private val tempInputDir = File("src/test/resources/someData")
        private val tempOutputDir = File("src/test/resources/someOutput")

        @BeforeClass
        @JvmStatic
        fun setup() {
            tempInputDir.mkdirs()
            repeat(numOfFiles) { index ->
                val newFile = File.createTempFile("someFile", ".java", tempInputDir)
                newFile.writeText("class someClass$index {\n")
                repeat(numOfMethods) {
                    newFile.appendText("public void someMethod${it + index * numOfMethods}() {} \n")
                }
                newFile.appendText("}")
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDown() {
            tempInputDir.deleteRecursively()
            tempOutputDir.deleteRecursively()
        }
    }
}
