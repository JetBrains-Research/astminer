package miningtool.examples

import miningtool.common.toPathContext
import miningtool.parse.antlr.java.JavaParser
import miningtool.parse.antlr.python.PythonParser
import miningtool.paths.PathMiner
import miningtool.paths.PathRetrievalSettings
import miningtool.paths.storage.VocabularyPathStorage
import java.io.File


fun allPythonFiles() {
    val folder = "./testData"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".py") }.forEach { file ->
        val node = PythonParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allPythonFiles")
}