package astminer.examples

import astminer.common.model.LabeledPathContexts
import astminer.parse.antlr.python.PythonParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.CsvPathStorage
import astminer.paths.toPathContext
import java.io.File


fun allPythonFiles() {
    val inputDir = "src/test/resources/examples/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val outputDir = "out_examples/allPythonFiles"
    val storage = CsvPathStorage(outputDir)

    File(inputDir).forFilesWithSuffix(".py") { file ->
        val node = PythonParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix
        val paths = miner.retrievePaths(node)

        storage.store(LabeledPathContexts(file.path, paths.map { toPathContext(it) }))
    }

    storage.close()
}
