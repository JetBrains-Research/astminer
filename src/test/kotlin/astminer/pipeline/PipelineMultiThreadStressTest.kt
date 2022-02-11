package astminer.pipeline

import astminer.common.model.DatasetHoldout
import astminer.config.*
import astminer.storage.MetaDataStorage
import astminer.storage.ast.JsonAstStorage
import astminer.storage.path.PathBasedStorage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.io.path.Path
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
        val actualNumOfAst = countLines(
            Path(
                outputPath,
                "java",
                DatasetHoldout.None.dirName,
                JsonAstStorage.AST_FILENAME
            ).toFile()
        )
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
            collectMetadata = true,
            numOfThreads = 8
        )
        Pipeline(config).run()
        val pathContextsPath = Path(
            outputPath,
            "java",
            DatasetHoldout.None.dirName,
            PathBasedStorage.PATH_CONTEXT_FILENAME
        )
        val expectedNumOfPathContexts = numOfFiles * numOfMethods
        val actualNumOfPathContexts = countLines(pathContextsPath.toFile())
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfPathContexts)

        val metadataPath = Path(
            outputPath,
            "java",
            DatasetHoldout.None.dirName,
            MetaDataStorage.METADATA_FILENAME
        )
        val actualNumOfMetadata = countLines(metadataPath.toFile())
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfMetadata)

        assertMethodOrder(pathContextsPath.toFile(), metadataPath.toFile())
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
            collectMetadata = true,
            numOfThreads = 8
        )
        Pipeline(config).run()
        val pathContextsPath = Path(
            outputPath,
            "java",
            DatasetHoldout.None.dirName,
            PathBasedStorage.PATH_CONTEXT_FILENAME
        )
        val expectedNumOfPathContexts = numOfFiles * numOfMethods
        val actualNumOfPathContexts = countLines(pathContextsPath.toFile())
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfPathContexts)

        val metadataPath = Path(
            outputPath,
            "java",
            DatasetHoldout.None.dirName,
            MetaDataStorage.METADATA_FILENAME
        )
        val actualNumOfMetadata = countLines(metadataPath.toFile())
        assertEquals(expected = expectedNumOfPathContexts.toLong(), actual = actualNumOfMetadata)

        assertMethodOrder(pathContextsPath.toFile(), metadataPath.toFile())
    }

    private fun countLines(file: File): Long {
        val reader = BufferedReader(FileReader(file))
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
        private val tempInputDir = Path("src", "test", "resources", "someData").toFile()
        private val tempOutputDir = Path("src", "test", "resources", "someOutput").toFile()

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
