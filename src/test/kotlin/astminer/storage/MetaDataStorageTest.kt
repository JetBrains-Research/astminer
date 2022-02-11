package astminer.storage

import astminer.common.model.DatasetHoldout
import astminer.storage.path.PathBasedStorage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Test
import kotlin.io.path.bufferedReader
import kotlin.io.path.createTempDirectory
import kotlin.io.path.pathString
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MetaDataStorageTest {
    @Test
    fun `Check metadata saving`() {
        val outputDir = createTempDirectory("metaData")
        val metaDataStorage = MetaDataStorage(outputDir.pathString)

        val mockedTrees = generateMockedResults(NUM_OF_MOCKED_RESULTS)
        mockedTrees.forEach { metaDataStorage.store(it) }

        val metaDataFile = outputDir
            .resolve(DatasetHoldout.None.dirName)
            .resolve(MetaDataStorage.METADATA_FILENAME)
            .bufferedReader()

        for ((jsonLine, result) in metaDataFile.lineSequence().zip(mockedTrees.asSequence())) {
            val json = Json.parseToJsonElement(jsonLine).jsonObject

            val path = json[MetaDataStorage.METADATA_FILENAME]?.jsonPrimitive?.content
            assertNotNull(path)
            assertEquals(result.filePath, path)

            val rawRange = json[PathBasedStorage.PATH_CONTEXT_FILENAME]?.jsonObject
            assertNotNull(rawRange)
            assertEquals(result.root.range, Json.decodeFromJsonElement(rawRange))
        }
        metaDataFile.close()
        outputDir.toFile().deleteRecursively()
    }

    companion object {
        const val NUM_OF_MOCKED_RESULTS = 1000
    }
}
