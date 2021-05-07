package astminer.pipeline

import astminer.storage.Storage
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files.createTempDirectory
import kotlin.test.assertEquals

internal class AbstractStorageCreatorTest {
    lateinit var tempDir: File

    @Before
    fun init() {
        tempDir = createTempDirectory("prefix").toFile()
    }

    @Test
    fun `test creating a StorageCreator should not alter the output directory`() {
        AbstractStorageCreatorImpl(tempDir.path)
        assertEquals(0, tempDir.listFiles()?.size, "There should be no files in the directory")
    }

    @Test
    fun `test StorageCreator's createStorageAndOutputFolder should create a subdirectory named after the file extension`() {
        AbstractStorageCreatorImpl(tempDir.path).createStorageAndOutputFolder("file extension")
        assertEquals(listOf("file extension"), tempDir.listFiles()?.map { it.name })
    }

    class AbstractStorageCreatorImpl(outputFolderPath: String) : AbstractStorageCreator(outputFolderPath) {
        override fun initializeStorage(outputFolderPath: String): Storage = DummyStorage()
    }
}
