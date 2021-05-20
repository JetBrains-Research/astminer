package astminer.examples

import astminer.cli.LabeledResult
import astminer.cli.MethodNameExtractor
import astminer.common.*
import astminer.parse.antlr.java.JavaFunctionSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun code2vecJavaMethods() {
    val folder = "src/test/resources/code2vecPathMining"
    val outputDir = "out_examples/code2vecPathMining"


    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    File(folder).forFilesWithSuffix(".java") { file ->
        //parse file
        val fileNode = JavaParser().parseInputStream(file.inputStream())

        //extract method nodes
        val functions = JavaFunctionSplitter().splitIntoFunctions(fileNode)

        val labelExtractor = MethodNameExtractor()

        functions.forEach { functionInfo ->
            val label = labelExtractor.extractLabel(functionInfo, file.absolutePath) ?: return@forEach
            // Retrieve paths from every node individually and store them
            storage.store(LabeledResult(functionInfo.root, label, file.absolutePath))
        }
    }

    storage.close()
}
