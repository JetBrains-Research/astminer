package astminer.examples

import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.storage.CsvPathStorage
import astminer.storage.CountingPathStorageConfig
import astminer.storage.identityTokenProcessor
import astminer.storage.labeledWithFilePath
import java.io.File

//Retrieve paths from Java files, using a generated parser.
fun allJavaFiles() {
    val inputDir = "src/test/resources/examples/"

    val outputDir = "out_examples/allJavaFilesAntlr"
    val storage = CsvPathStorage(outputDir, CountingPathStorageConfig(5, 5))

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
        storage.store(node.labeledWithFilePath(file.path))
    }

    storage.close()
}
