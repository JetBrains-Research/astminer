package astminer.examples

import astminer.common.model.LabeledPathContexts
import astminer.parse.antlr.javascript.JavaScriptParser
import astminer.paths.CsvPathStorage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import java.io.File

fun allJavaScriptFiles() {
    val folder = "src/test/resources/examples"
    val outputDir = "out_examples/allJavaScriptFilesAntlr"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = CsvPathStorage(outputDir)

    File(folder).forFilesWithSuffix(".js") {file ->
        val node = JavaScriptParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix
        val paths = miner.retrievePaths(node)

        storage.store(LabeledPathContexts(file.path, paths.map { toPathContext(it) }))
    }

    storage.close()
}