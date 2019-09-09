@file:JvmName("CppExample")

package astminer.examples

import astminer.parse.cpp.FuzzyCppParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import astminer.paths.toPathContext
import java.io.File

// Retrieve paths from .cpp preprocessed files, using a fuzzyc2cpg parser.
fun allCppFiles() {
    val folder = File("testData/examples/cpp")

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()
    val parser = FuzzyCppParser()
    val preprocOutputFolder = File("preprocessed")

    parser.preprocessProject(folder, preprocOutputFolder)

    val parsedFiles = parser.parseWithExtension(preprocOutputFolder, "cpp")

    parsedFiles.forEach { parseResult ->
        if (parseResult.root == null) {
            return@forEach
        }
        val paths = miner.retrievePaths(parseResult.root)

        storage.store(paths.map { toPathContext(it) }, entityId = parseResult.filePath)
    }

    storage.save("out_examples/allCppFiles")
}
