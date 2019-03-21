@file:JvmName("PyExample")

package astminer.examples.pyExample

import astminer.paths.toPathContext
import astminer.parse.antlr.java.JavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import java.io.File

// Retrieve paths from two Java projects for further usage in python example.
fun processPyExampleData() {
    val folder = "./py_example/data/"

    val miner = PathMiner(PathRetrievalSettings(8, 3))
    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".java") }.forEach { file ->
        val node = JavaParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("py_example/processed_data/")
}

fun main(args: Array<String>) {
    processPyExampleData()
}