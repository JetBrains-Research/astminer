package astminer.pipeline

import astminer.common.getProjectFilesWithExtension
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import java.io.Closeable
import java.io.File

class ProjectImporter(val projectDirectory: File, private val withPreprocessing: Boolean) : Closeable {
    companion object {
        private val preprocessedExtensions = listOf("c", "cpp")
        private val folderForPreprocessedFiles = File("temp")
    }

    private var preprocessingComplete = false

    private fun preprocess() {
        val parser = FuzzyCppParser()
        parser.preprocessProject(projectDirectory, folderForPreprocessedFiles)
        preprocessingComplete = true
    }

    private fun getFolder(extension: String): File =
        if (withPreprocessing && extension in preprocessedExtensions) {
            if (!preprocessingComplete) {
                preprocess()
            }
            folderForPreprocessedFiles
        } else {
            projectDirectory
        }


    fun getFiles(extension: String): Sequence<File> =
        getProjectFilesWithExtension(getFolder(extension), extension).asSequence()

    override fun close() {
        folderForPreprocessedFiles.delete()
    }
}
