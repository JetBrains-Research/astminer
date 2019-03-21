package astminer.examples

import astminer.common.toPathContext
import astminer.parse.antlr.java.JavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.storage.VocabularyPathStorage
import java.io.File

//Retrieve paths from Java files, using a generated parser.
fun allJavaFiles() {
    val folder = "./testData/examples/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".java") }.forEach { file ->
        val node = JavaParser().parse(file.inputStream()) ?: return@forEach
        val paths = miner.retrievePaths(node)

        storage.store(paths.map { toPathContext(it) }, entityId = file.path)
    }

    storage.save("out_examples/allJavaFilesAntlr")
}