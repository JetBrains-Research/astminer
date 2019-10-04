package astminer.examples

import astminer.common.model.LabeledPathContexts
import astminer.parse.java.GumTreeJavaParser
import astminer.parse.java.GumTreeMethodSplitter
import astminer.parse.java.MethodInfo
import astminer.parse.java.getMethodInfo
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.CsvPathStorage
import astminer.paths.toPathContext
import java.io.File


private fun getCsvFriendlyMethodId(methodInfo: MethodInfo?): String {
    if (methodInfo == null) return "unknown_method"
    return "${methodInfo.enclosingClassName}.${methodInfo.methodName}(${methodInfo.parameterTypes.joinToString("|")})"
}


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun allJavaMethods() {
    val inputDir = "./testData/gumTreeMethodSplitter"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val outputDir = "out_examples/allJavaMethods"
    val storage = CsvPathStorage(outputDir)

    File(inputDir).forFilesWithSuffix(".java") { file ->
        //parse file
        val fileNode = GumTreeJavaParser().parse(file.inputStream()) ?: return@forFilesWithSuffix

        //extract method nodes
        val methodNodes = GumTreeMethodSplitter().split(fileNode)

        methodNodes.forEach { methodNode ->
            //Retrieve paths from every node individually
            val paths = miner.retrievePaths(methodNode)
            //Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodNode.getMethodInfo())}"
            storage.store(LabeledPathContexts(entityId, paths.map { toPathContext(it) }))
        }
    }

    storage.save()
}
