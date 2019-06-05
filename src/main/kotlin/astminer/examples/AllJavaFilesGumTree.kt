package astminer.examples

import astminer.parse.java.GumTreeJavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import astminer.paths.toPathContext
import java.io.File

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val folder = "./testData/gumTreeMethodSplitter/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).forFilesWithSuffix(".java") { file ->
        val node = GumTreeJavaParser().parse(file.inputStream()) ?: return@forFilesWithSuffix
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allJavaFilesGumTree")
}