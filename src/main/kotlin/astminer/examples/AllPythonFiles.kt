package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.parse.antlr.python.PythonParser
import astminer.storage.CsvPathStorage
import astminer.storage.PathBasedStorageConfig
import astminer.storage.labeledWithFilePath
import astminer.storage.splitTokenProcessor
import java.io.File


fun allPythonFiles() {
    val inputDir = "src/test/resources/examples/"

    val outputDir = "out_examples/allPythonFiles"
    val storage = CsvPathStorage(outputDir, PathBasedStorageConfig(5, 5), splitTokenProcessor)

    val files = getProjectFilesWithExtension(File(inputDir), "py")
    PythonParser().parseFiles(files) { parseResult ->
        parseResult.labeledWithFilePath()?.let {
            storage.store(it)
        }
    }

    storage.close()
}
