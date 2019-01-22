package miningtool.examples

import miningtool.common.toPathContext
import miningtool.parse.java.GumTreeJavaParser
import miningtool.paths.PathMiner
import miningtool.paths.PathRetrievalSettings
import miningtool.paths.storage.VocabularyPathStorage
import java.io.File

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val folder = "./testData/examples/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".java") }.forEach { file ->
        val node = GumTreeJavaParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allJavaFilesGumTree")
}