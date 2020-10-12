@file:JvmName("CppExample")

package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.common.model.LabeledPathContexts
import astminer.parse.cpp.FuzzyCppParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.CsvPathStorage
import astminer.paths.toPathContext
import java.io.File

// Retrieve paths from .cpp preprocessed files, using a fuzzyc2cpg parser.
fun allCppFiles() {
    val inputDir = File("src/test/resources/examples/cpp")

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val outputDir = "out_examples/allCppFiles"
    val storage = CsvPathStorage(outputDir)
    val parser = FuzzyCppParser()
    val preprocOutputFolder = File("preprocessed")

    parser.preprocessProject(inputDir, preprocOutputFolder)

    val files = getProjectFilesWithExtension(preprocOutputFolder, "cpp")

    parser.parseFiles(files) { parseResult ->
        if (parseResult.root != null) {
            val paths = miner.retrievePaths(parseResult.root)
            storage.store(LabeledPathContexts(parseResult.filePath, paths.map { toPathContext(it) }))
        }
    }

    storage.close()
}
