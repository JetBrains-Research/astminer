package astminer.storage.path

import astminer.common.model.AdditionalStorageParameters
import org.junit.Test
import kotlin.io.path.createTempDirectory
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString

class Code2VecStorageTest {
    @Test
    fun `Check metadata saving`() {
        val outputDir = createTempDirectory(prefix = "pathTest")
        checkPathBasedMetadataSaving(
            Code2VecPathStorage(
                outputDirectoryPath = outputDir.pathString,
                config = PathBasedStorageConfig(10, 10),
                metaDataConfig = AdditionalStorageParameters(storeRanges = true, storePaths = true)
            )
        )
        outputDir.deleteIfExists()
    }
}
