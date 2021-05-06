package astminer.pipeline

import astminer.config.Code2VecPathStorageConfig
import astminer.config.CsvAstStorageConfig
import astminer.config.DotAstStorageConfig
import astminer.storage.ast.CsvAstStorage
import astminer.storage.ast.DotAstStorage
import astminer.storage.path.Code2VecPathStorage
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class StorageCreatorImplTest {

    lateinit var tempDir: File

    @Before
    fun init() {
        tempDir = createTempDir()
    }

    @Test
    fun `test creating a StorageCreator should not alter the output directory`() {
        StorageCreatorImpl(CsvAstStorageConfig, tempDir.path)
        assertEquals(0, tempDir.listFiles()?.size, "There should be no files in the directory")
    }

    @Test
    fun `test StorageCreator's createStorageAndOutputFolder should create a subdirectory named after the file extension`() {
        StorageCreatorImpl(CsvAstStorageConfig, tempDir.path).createStorageAndOutputFolder("file extension")
        assertEquals(listOf("file extension"), tempDir.listFiles()?.map { it.name })
    }

    @Test
    fun `test StorageCreator should create CsvAstStorage if given CsvAstStorageConfig`() {
        val storage = StorageCreatorImpl(CsvAstStorageConfig, tempDir.path).createStorageAndOutputFolder("a")
        assertTrue { storage is CsvAstStorage }
    }

    @Test
    fun `test StorageCreator should create DotAstStorage if given DotAstStorageConfig`() {
        val storage = StorageCreatorImpl(DotAstStorageConfig(), tempDir.path).createStorageAndOutputFolder("a")
        assertTrue { storage is DotAstStorage }
    }

    @Test
    fun `test StorageCreator should create Code2VecPathStorage if given Code2VecPathStorageConfig`() {
        val config = Code2VecPathStorageConfig(1, 1)
        val storage = StorageCreatorImpl(config, tempDir.path).createStorageAndOutputFolder("a")
        assertTrue { storage is Code2VecPathStorage }
    }
}
