package astminer.examples

import astminer.parse.antlr.javascript.JSParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import astminer.paths.toPathContext
import java.io.File

fun allJSFiles() {
    val folder = "./testData/examples"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).forFilesWithSuffix(".js") {file ->
        val node = JSParser().parse(file.inputStream()) ?: return@forFilesWithSuffix
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allJSFilesAntlr")
}