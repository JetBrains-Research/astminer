package astminer.storage.path

import astminer.common.model.DatasetHoldout
import astminer.storage.generateMockedResults
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private const val NUM_OF_MOCKED_RESULTS = 100

fun checkPathBasedMetadataSaving(pathBasedStorage: PathBasedStorage) {
    val mockedTrees = generateMockedResults(NUM_OF_MOCKED_RESULTS)
    mockedTrees.forEach { pathBasedStorage.store(it) }
    val outputDirectory = File(pathBasedStorage.outputDirectoryPath)
    val metaDataFile = outputDirectory.resolve(DatasetHoldout.None.dirName).resolve(METADATA_FILE_NAME).bufferedReader()
    for ((jsonLine, result) in metaDataFile.lineSequence().zip(mockedTrees.asSequence())) {
        val json = Json.parseToJsonElement(jsonLine).jsonObject

        val path = json[METADATA_PATH_FIELD]?.jsonPrimitive?.content
        assertNotNull(path)
        assertEquals(result.filePath, path)

        val rawRange = json[METADATA_RANGE_FIELD]?.jsonObject
        assertNotNull(rawRange)
        assertEquals(result.root.range, Json.decodeFromJsonElement(rawRange))
    }
    metaDataFile.close()
    outputDirectory.deleteRecursively()
}
