package astminer.pipeline

import astminer.config.*
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import kotlin.test.AfterTest
import kotlin.test.assertEquals

class PipelineAsyncStressTest {
    @AfterTest
    fun deleteOutput() {
        tempOutputDir.deleteRecursively()
    }

    @Test
    fun jsonStorageTest() {
        val config = PipelineConfig(
            inputDir = tempInputDir.path,
            outputDir = tempOutputDir.path,
            parser = ParserConfig(
                name = ParserType.Antlr,
                languages = listOf(FileExtension.Java)
            ),
            filters = listOf(),
            labelExtractor = FunctionNameExtractorConfig(),
            storage = JsonAstStorageConfig()
        )
        Pipeline(config).run()
        assertEquals((numOfFiles * numOfMethods).toLong(), countLines("${tempOutputDir.path}/java/asts.jsonl"))
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
        }
    }
}
