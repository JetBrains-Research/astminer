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
    val folder = "testData/examples/cpp/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()
    val parser = FuzzyCppParser()
    val preprocOutputFolder = "${System.getProperty("user.dir")}/$folder${parser.preprocessDirName}"


    File(folder).forFilesWithSuffix(".cpp") { file ->
        parser.preprocessWithoutIncludes(file, preprocOutputFolder)
    }

    val parsedFiles = parser.parse(listOf(preprocOutputFolder))

    parsedFiles.forEach { node ->
        if (node == null) {
            return@forEach
        }
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = node.getToken())
    }

    storage.save("out_examples/allCppFiles")
}
