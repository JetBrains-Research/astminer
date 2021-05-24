package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.parse.antlr.python.PythonParser
import astminer.storage.path.PathBasedStorageConfig
import astminer.storage.path.Code2VecPathStorage
import java.io.File


fun allPythonFiles() {
    val inputDir = "src/test/resources/examples/"

    val outputDir = "out_examples/allPythonFiles"
    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    val files = getProjectFilesWithExtension(File(inputDir), "py")
    PythonParser().parseFiles(files) { parseResult ->
        storage.store(parseResult.labeledWithFilePath())
    }

    storage.close()
}
