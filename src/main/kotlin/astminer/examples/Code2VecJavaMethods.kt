package astminer.examples

import astminer.common.*
import astminer.common.model.LabeledPathContexts
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.paths.*
import java.io.File


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun code2vecJavaMethods() {
    val folder = "src/test/resources/code2vecPathMining"
    val outputDir = "out_examples/code2vecPathMining"

    val miner = PathMiner(PathRetrievalSettings(5, 5))

    val storage = Code2VecPathStorage(outputDir)

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

            // Retrieve paths from every node individually
            val paths = miner.retrievePaths(methodRoot)
            storage.store(LabeledPathContexts(label, paths.map { toPathContext(it) { node -> node.getNormalizedToken() } }))
        }
    }

    storage.close()
}
