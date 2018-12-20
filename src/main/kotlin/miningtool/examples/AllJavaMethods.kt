package miningtool.examples

import miningtool.common.toPathContext
import miningtool.parse.java.GumTreeJavaParser
import miningtool.parse.java.GumTreeMethodSplitter
import miningtool.parse.java.MethodInfo
import miningtool.parse.java.getMethodInfo
import miningtool.paths.PathMiner
import miningtool.paths.PathRetrievalSettings
import miningtool.paths.storage.VocabularyPathStorage
import java.io.File


private fun getCsvFriendlyMethodId(methodInfo: MethodInfo?): String {
    if (methodInfo == null) return "unknown_method"
    return "${methodInfo.enclosingClassName}.${methodInfo.methodName}(${methodInfo.parameterTypes.joinToString("|")})"
}


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun allJavaMethods() {
    val folder = "./testData"

    val miner = PathMiner(PathRetrievalSettings(5, 5))

    val storage = VocabularyPathStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".java") }.forEach { file ->
        //parse file
        val fileNode = GumTreeJavaParser().parse(file.inputStream()) ?: return@forEach

        //extract method nodes
        val methodNodes = GumTreeMethodSplitter().split(fileNode)

        methodNodes.forEach { methodNode ->
            //Retrieve paths from every node individually
            val paths = miner.retrievePaths(methodNode)
            //Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodNode.getMethodInfo())}"
            storage.store(paths.map { toPathContext(it) }, entityId)
        }
    }

    storage.save("out_examples/allJavaMethods")
}