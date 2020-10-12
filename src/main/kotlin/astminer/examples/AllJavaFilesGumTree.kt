package astminer.examples

import astminer.common.model.LabeledPathContexts
import astminer.parse.java.GumTreeJavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.CsvPathStorage
import astminer.paths.toPathContext
import java.io.File

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val outputDir = "out_examples/allJavaFilesGumTree"
    val storage = CsvPathStorage(outputDir)

    File(inputDir).forFilesWithSuffix(".java") { file ->
        val node = GumTreeJavaParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix
        val paths = miner.retrievePaths(node)

        storage.store(LabeledPathContexts(file.path, paths.map { toPathContext(it) }))
    }

    storage.close()
}
