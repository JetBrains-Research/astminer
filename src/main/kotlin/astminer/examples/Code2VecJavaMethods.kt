package astminer.examples

import astminer.cli.LabeledResult
import astminer.common.*
import astminer.parse.antlr.java.JavaMethodSplitter
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
        val fileNode = JavaParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix

        //extract method nodes
        val methods = JavaMethodSplitter().splitIntoMethods(fileNode)

        methods.forEach { methodInfo ->
            val methodNameNode = methodInfo.method.nameNode ?: return@forEach
            val methodRoot = methodInfo.method.root
            val label = splitToSubtokens(methodNameNode.getToken()).joinToString("|")
            methodRoot.preOrder().forEach { it.setNormalizedToken() }
            methodNameNode.setNormalizedToken("METHOD_NAME")

            // Retrieve paths from every node individually and store them
            storage.store(LabeledResult(methodRoot, label, file.absolutePath))
        }
    }

    storage.close()
}
