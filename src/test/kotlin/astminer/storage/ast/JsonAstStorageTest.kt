package astminer.storage.ast

import astminer.common.model.AdditionalStorageParameters
import astminer.common.model.LabeledResult
import astminer.common.model.NodeRange
import astminer.common.model.Position
import astminer.storage.tree
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
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
    private val startField = "start"
    private val endField = "end"
    private val lineField = "l"
    private val columnField = "c"

    @Test
    fun `Check correct metadata saving`() {
        val mockedResults = List(numOfMockedTrees) {
            LabeledResult(
                root = tree {
                    typeLabel = "mockedNode"
                    range = NodeRange(
                        Position(it, it),
                        Position(it + 1, it + 1)
                    )
                },
                label = "$it",
                filePath = "/$it/$it/$it"
            )
        }

        val outputDirFile = createTempDirectory(prefix = "jsonTest").toFile()
        val jsonStorage = JsonAstStorage(
            outputDirFile.path,
            AdditionalStorageParameters(
                storeRanges = true,
                storePaths = true
            )
        )
        mockedResults.forEach { jsonStorage.store(it) }
        val astFile = outputDirFile.resolve("data").resolve("asts.jsonl").bufferedReader()
        for ((jsonLine, tree) in astFile.lineSequence().zip(mockedResults.asSequence())) {
            val json = Json.parseToJsonElement(jsonLine).jsonObject

            val label = json[labelField]?.jsonPrimitive?.content
            assertNotNull(label)
            assertEquals(tree.label, label)

            val path = json[pathField]?.jsonPrimitive?.content
            assertNotNull(path)
            assertEquals(tree.filePath, path)

            // Checking range correctness only for root
            val rawRange = json[astField]?.jsonArray?.get(0)?.jsonObject?.get(rangeField)?.jsonObject
            assertNotNull(rawRange)
            val (start, end) = rawRange[startField]?.jsonObject to rawRange[endField]?.jsonObject
            assertNotNull(start)
            assertNotNull(end)
            val (sl, sc) = start[lineField]?.jsonPrimitive?.content to start[columnField]?.jsonPrimitive?.content
            val (el, ec) = end[lineField]?.jsonPrimitive?.content to end[columnField]?.jsonPrimitive?.content
            assertNotNull(sl)
            assertNotNull(sc)
            assertNotNull(el)
            assertNotNull(ec)
            val extractedRange = NodeRange(
                Position(sl.toInt(), sc.toInt()),
                Position(el.toInt(), ec.toInt())
            )
            assertEquals(tree.root.range, extractedRange)
        }
        astFile.close()
        outputDirFile.deleteRecursively()
        outputDirFile.delete()
    }
}