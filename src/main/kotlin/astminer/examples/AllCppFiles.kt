@file:JvmName("CppExample")

package astminer.examples

import astminer.parse.antlr.joern.parseJoernAst
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import astminer.paths.toPathContext

// Retrieve paths from .cpp files, using a generated parser.
fun main(args: Array<String>) {
    val folder = "testData/examples/cpp/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = VocabularyPathStorage()

    val parsedFiles = parseJoernAst(folder)
    parsedFiles.forEach { node ->
        if (node == null) {
            return@forEach
        }
        val paths = miner.retrievePaths(node)

        // Root node is of type File and stores path to the file.
        storage.store(paths.map { toPathContext(it) }, entityId = node.getToken())
    }

    storage.save("out_examples/allCppFiles")
}