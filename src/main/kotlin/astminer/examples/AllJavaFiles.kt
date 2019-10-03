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
    val folder = "./testData/examples/"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val storage = CsvPathStorage()

    File(folder).forFilesWithSuffix("11.java") { file ->
        val node = JavaParser().parse(file.inputStream()) ?: return@forFilesWithSuffix
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

    storage.save("out_examples/allJavaFilesAntlr")
}