package astminer.examples

import astminer.common.toPathContext
import astminer.parse.antlr.joern.CppParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.storage.VocabularyPathStorage
import java.io.File

//Retrieve paths from .cpp files, using a generated parser.
fun allCppFiles() {
    val folder = "./testData/examples/cpp/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".cpp") }.forEach { file ->
        val node = CppParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allCppFiles")
}