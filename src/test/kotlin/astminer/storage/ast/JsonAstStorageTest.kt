package astminer.storage.ast

import astminer.common.model.*
import astminer.storage.generateMockedResults
import kotlinx.serialization.json.*
import org.junit.Test
import kotlin.io.path.createTempDirectory
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JsonAstStorageTest {
    private val numOfMockedTrees = 100

    private val pathField = "path"
    private val astField = "ast"
    private val labelField = "label"
    private val rangeField = "range"

    @Test
    fun `Check correct metadata saving`() {
        val mockedResults = generateMockedResults(numOfMockedTrees)

        val outputDirFile = createTempDirectory(prefix = "jsonTest").toFile()
        val jsonStorage = JsonAstStorage(
            outputDirFile.path,
            AdditionalStorageParameters(
                storeRanges = true,
                storePaths = true
            )
        )
        mockedResults.forEach { jsonStorage.store(it) }
        val astFile = outputDirFile.resolve(DatasetHoldout.None.dirName).resolve("asts.jsonl").bufferedReader()
        for ((jsonLine, result) in astFile.lineSequence().zip(mockedResults.asSequence())) {
            val json = Json.parseToJsonElement(jsonLine).jsonObject

            val label = json[labelField]?.jsonPrimitive?.content
            assertNotNull(label)
            assertEquals(result.label, label)

            val path = json[pathField]?.jsonPrimitive?.content
            assertNotNull(path)
            assertEquals(result.filePath, path)

            // Checking range correctness only for root
            val rawRange = json[astField]?.jsonArray?.get(0)?.jsonObject?.get(rangeField)?.jsonObject
            assertNotNull(rawRange)
            val extractedRange = Json.decodeFromJsonElement<NodeRange>(rawRange)
            assertEquals(result.root.range, extractedRange)
        }
        astFile.close()
        outputDirFile.deleteRecursively()
        outputDirFile.delete()
    }
}