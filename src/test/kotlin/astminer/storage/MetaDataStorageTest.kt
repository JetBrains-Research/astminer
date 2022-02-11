package astminer.storage

import astminer.common.model.DatasetHoldout
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.io.path.bufferedReader
import kotlin.io.path.createTempDirectory
import kotlin.io.path.pathString
import kotlin.test.assertEquals

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
            val treeMetaData = Json.decodeFromString<TreeMetaData>(jsonLine)
            val expectedTreeMetaData = TreeMetaData(result)
            assertEquals(expectedTreeMetaData, treeMetaData)
        }
        metaDataFile.close()
        outputDir.toFile().deleteRecursively()
    }

    companion object {
        const val NUM_OF_MOCKED_RESULTS = 1000
    }
}
