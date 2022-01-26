package astminer.pipeline

import astminer.common.model.MetaDataConfig
import astminer.config.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
                maxPathWidth = 1000,
                metadata = MetaDataConfig(
                    storePaths = true,
                    storeRanges = true
                )
            ),
            numOfThreads = 8
        )
        Pipeline(config).run()
        val pathContextsPath = "$outputPath/java/data/path_contexts.c2s"
        val expectedNumOfPathContexts = numOfFiles * numOfMethods
        val actualNumOfPathContexts = countLines(pathContextsPath)
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfPathContexts)

        val metadataPath = "$outputPath/java/data/metadata.jsonl"
        val actualNumOfMetadata = countLines(metadataPath)
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfMetadata)

        assertMethodOrder(File(pathContextsPath), File(metadataPath))
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
                maxPathWidth = 1000,
                metadata = MetaDataConfig(
                    storePaths = true,
                    storeRanges = true
                )
            ),
            numOfThreads = 8
        )
        Pipeline(config).run()
        val pathContextsPath = "$outputPath/java/data/$pathContextsFileName"
        val expectedNumOfPathContexts = numOfFiles * numOfMethods
        val actualNumOfPathContexts = countLines(pathContextsPath)
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfPathContexts)

        val metadataPath = "$outputPath/java/data/$metadataFileName"
        val actualNumOfMetadata = countLines(metadataPath)
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfMetadata)

        assertMethodOrder(File(pathContextsPath), File(metadataPath))
    }

    private fun countLines(filePath: String): Long {
        val reader = BufferedReader(FileReader(filePath))
        var numOfLines = 0L
        while (reader.readLine() != null) { numOfLines++ }
        return numOfLines
    }

    private fun assertMethodOrder(pathContexts: File, metadata: File) {
        val pathReader = pathContexts.bufferedReader()
        val metaReader = metadata.bufferedReader()
        for ((path, metaline) in pathReader.lineSequence().zip(metaReader.lineSequence())) {
            val expectedMethodName = path.split(" ")[0]
            val actualMethodName = Json.parseToJsonElement(metaline).jsonObject["label"]?.jsonPrimitive?.content
            assertEquals(expectedMethodName, actualMethodName)
        }
        pathReader.close()
        metaReader.close()
    }

    companion object {
        private const val numOfFiles = 3000
        private const val numOfMethods = 100
        private const val methodNameLength = 10
        private val tempInputDir = File("src/test/resources/someData")
        private val tempOutputDir = File("src/test/resources/someOutput")
        private const val pathContextsFileName = "path_contexts.c2s"
        private const val metadataFileName = "metadata.jsonl"

        private fun getRandomString(length: Int): String {
            val allowedChars = ('A'..'Z') + ('a'..'z')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }

        @BeforeClass
        @JvmStatic
        fun setup() {
            tempInputDir.mkdirs()
            repeat(numOfFiles) { index ->
                val newFile = File.createTempFile("someFile", ".java", tempInputDir)
                newFile.writeText("class someClass$index {\n")
                repeat(numOfMethods) {
                    newFile.appendText("public void ${getRandomString(methodNameLength)}() {} \n")
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
