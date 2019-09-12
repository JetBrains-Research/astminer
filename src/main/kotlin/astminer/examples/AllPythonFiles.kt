package astminer.examples

import astminer.common.LabeledPathContexts
import astminer.parse.antlr.python.PythonParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.CsvPathStorage
import astminer.paths.toPathContext
import java.io.File


fun allPythonFiles() {
    val folder = "./testData/examples/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = CsvPathStorage()

    File(folder).forFilesWithSuffix(".py") { file ->
        val node = PythonParser().parse(file.inputStream()) ?: return@forFilesWithSuffix
        val paths = miner.retrievePaths(node)

        storage.store(LabeledPathContexts(file.path, paths.map { toPathContext(it) }))
    }




    storage.save("out_examples/allPythonFiles")
}