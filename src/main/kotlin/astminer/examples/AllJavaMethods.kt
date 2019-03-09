package astminer.examples

import astminer.paths.toPathContext
import astminer.parse.java.GumTreeJavaParser
import astminer.parse.java.GumTreeMethodSplitter
import astminer.parse.java.MethodInfo
import astminer.parse.java.getMethodInfo
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.VocabularyPathStorage
import java.io.File


private fun getCsvFriendlyMethodId(methodInfo: MethodInfo?): String {
    if (methodInfo == null) return "unknown_method"
    return "${methodInfo.enclosingClassName}.${methodInfo.methodName}(${methodInfo.parameterTypes.joinToString("|")})"
}


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun allJavaMethods() {
    val folder = "./testData/gumTreeMethodSplitter"

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