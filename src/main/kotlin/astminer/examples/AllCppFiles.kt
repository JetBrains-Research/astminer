@file:JvmName("CppExample")

package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.common.model.Node
import astminer.parse.cpp.FuzzyCppParser
import astminer.storage.*
import java.io.File

// Retrieve paths from .cpp preprocessed files, using a fuzzyc2cpg parser.
fun allCppFiles() {
    val inputDir = File("src/test/resources/examples/cpp")

    val outputDir = "out_examples/allCppFiles"
    val storage = CsvPathStorage(outputDir, CountingPathStorageConfig(5, 5))
    val parser = FuzzyCppParser()
    val preprocOutputFolder = File("preprocessed")

    parser.preprocessProject(inputDir, preprocOutputFolder)

    val files = getProjectFilesWithExtension(preprocOutputFolder, "cpp")

    parser.parseFiles(files) { parseResult ->
        parseResult.labeledWithFilePath()?.let {
            storage.store(it)
        }
    }

    storage.close()
}
