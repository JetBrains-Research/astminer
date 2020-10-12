package astminer.examples

import astminer.common.model.LabeledPathContexts
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.CsvPathStorage
import astminer.paths.toPathContext
import java.io.File

//Retrieve paths from Java files, using a generated parser.
fun allJavaFiles() {
    val inputDir = "src/test/resources/examples/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val outputDir = "out_examples/allJavaFilesAntlr"
    val storage = CsvPathStorage(outputDir)

    File(inputDir).forFilesWithSuffix("11.java") { file ->
        val node = JavaParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix
        val paths = miner.retrievePaths(node)
        node.prettyPrint()
        JavaMethodSplitter().splitIntoMethods(node).forEach {
            println(it.name())
            println(it.returnType())
            println(it.enclosingElementName())
            it.methodParameters.forEach { parameters ->
                println("${parameters.name()} ${parameters.returnType()}")
            }
        }
        storage.store(LabeledPathContexts(file.path, paths.map { toPathContext(it) }))
    }

    storage.close()
}
