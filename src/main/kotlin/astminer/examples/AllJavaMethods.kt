package astminer.examples

import astminer.cli.LabeledResult
import astminer.common.model.MethodInfo
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.parse.gumtree.java.GumTreeJavaMethodSplitter
import astminer.storage.*
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File


private fun getCsvFriendlyMethodId(methodInfo: MethodInfo<GumTreeNode>): String {
    val className = methodInfo.enclosingElementName() ?: ""
    val methodName = methodInfo.name() ?: "unknown_method"
    val parameterTypes = methodInfo.methodParameters.joinToString("|") { it.name() ?: "_" }
    return "$className.$methodName($parameterTypes)"
}


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun allJavaMethods() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter"

    val outputDir = "out_examples/allJavaMethods"
    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5), TokenProcessor.Split)

    File(inputDir).forFilesWithSuffix(".java") { file ->
        //parse file
        val fileNode = GumTreeJavaParser().parseInputStream(file.inputStream())

        //extract method nodes
        val methodNodes = GumTreeJavaMethodSplitter().splitIntoMethods(fileNode)

        methodNodes.forEach { methodInfo ->
            //Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodInfo)}"
            val labelingResult = LabeledResult(fileNode, entityId, file.path)
            storage.store(labelingResult)
        }
    }

    storage.close()
}
