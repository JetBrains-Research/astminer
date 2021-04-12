package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.parse.java.GumTreeJavaParser
import astminer.storage.path.CsvPathStorage
import astminer.storage.path.PathBasedStorageConfig
import astminer.storage.labeledWithFilePath
import java.io.File

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter/"

    val outputDir = "out_examples/allJavaFilesGumTree"
    val storage = CsvPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    val files = getProjectFilesWithExtension(File(inputDir), "java")
    GumTreeJavaParser().parseFiles(files) { parseResult ->
        parseResult.labeledWithFilePath()?.let {
            storage.store(it)
        }
    }

    storage.close()
}
