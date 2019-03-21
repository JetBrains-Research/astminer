package astminer.examples

import astminer.paths.toPathContext
import astminer.parse.antlr.python.PythonParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import java.io.File


fun allPythonFiles() {
    val folder = "./testData/examples/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".py") }.forEach { file ->
        val node = PythonParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allPythonFiles")
}