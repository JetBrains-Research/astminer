@file:JvmName("CppExample")

package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

// Retrieve paths from .cpp preprocessed files, using a fuzzyc2cpg parser.
fun allCppFiles() {
    val inputDir = File("src/test/resources/examples/cpp")

    val outputDir = "out_examples/allCppFiles"
    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))
    val parser = FuzzyCppParser()
    val preprocOutputFolder = File("preprocessed")

    parser.preprocessProject(inputDir, preprocOutputFolder)

    val files = getProjectFilesWithExtension(preprocOutputFolder, "cpp")

    parser.parseFiles(files) { parseResult ->
        storage.store(parseResult.labeledWithFilePath())
    }

    storage.close()
}
