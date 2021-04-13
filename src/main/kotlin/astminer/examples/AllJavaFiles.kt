package astminer.examples

import astminer.cli.LabeledResult
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.storage.path.CsvPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

//Retrieve paths from Java files, using a generated parser.
fun allJavaFiles() {
    val inputDir = "src/test/resources/examples/"

    val outputDir = "out_examples/allJavaFilesAntlr"
    val storage = CsvPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    File(inputDir).forFilesWithSuffix("11.java") { file ->
        val node = JavaParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix
        node.prettyPrint()
        JavaMethodSplitter().splitIntoMethods(node).forEach {
            println(it.name())
            println(it.returnType())
            println(it.enclosingElementName())
            it.methodParameters.forEach { parameters ->
                println("${parameters.name()} ${parameters.returnType()}")
            }
        }
        storage.store(LabeledResult(node, file.path, file.path))
    }

    storage.close()
}
