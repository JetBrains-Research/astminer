package astminer.examples

import astminer.paths.toPathContext
import astminer.parse.java.GumTreeJavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import java.io.File

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val folder = "./testData/gumTreeMethodSplitter/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".java") }.forEach { file ->
        val node = GumTreeJavaParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allJavaFilesGumTree")
}