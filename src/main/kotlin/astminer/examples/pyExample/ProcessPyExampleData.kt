@file:JvmName("PyExample")

package astminer.examples.pyExample

import astminer.common.model.LabeledPathContexts
import astminer.paths.toPathContext
import astminer.parse.antlr.java.JavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.CsvPathStorage
import java.io.File

// Retrieve paths from two Java projects for further usage in python example.
fun processPyExampleData() {
    val inputDir = "./py_example/data/"

    val miner = PathMiner(PathRetrievalSettings(8, 3))
    val outputDir = "py_example/processed_data/"
    val storage = CsvPathStorage(outputDir)

    File(inputDir).walkTopDown().filter { it.path.endsWith(".java") }.forEach { file ->
        val node = JavaParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(LabeledPathContexts(file.path, paths.map { toPathContext(it) }))
    }

    storage.close()
}

fun main(args: Array<String>) {
    processPyExampleData()
}
