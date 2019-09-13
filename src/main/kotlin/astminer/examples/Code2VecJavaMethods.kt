package astminer.examples

import astminer.common.*
import astminer.parse.java.GumTreeJavaParser
import astminer.parse.java.GumTreeMethodSplitter
import astminer.parse.java.MethodInfo
import astminer.parse.java.getMethodInfo
import astminer.paths.*
import java.io.File


private fun getCsvFriendlyMethodId(methodInfo: MethodInfo?): String {
    if (methodInfo == null) return "unknown_method"
    return "${methodInfo.enclosingClassName}.${methodInfo.methodName}(${methodInfo.parameterTypes.joinToString("|")})"
}


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun code2vecJavaMethods() {
    val folder = "./testData/gumTreeMethodSplitter"

    val miner = PathMiner(PathRetrievalSettings(5, 5))

    val storage = Code2VecPathStorage()

    File(folder).forFilesWithSuffix(".java") { file ->
        //parse file
        val fileNode = GumTreeJavaParser().parse(file.inputStream()) ?: return@forFilesWithSuffix

        //extract method nodes
        val methodNodes = GumTreeMethodSplitter().split(fileNode)

        methodNodes.forEach { methodNode ->
            val methodNameNode = methodNode.getChildren().firstOrNull {
                it.getTypeLabel() == "SimpleName"
            } ?: return@forEach
            val label = splitToSubtokens(methodNameNode.getToken()).joinToString("|")
            methodNode.preOrder().forEach { it.setNormalizedToken() }
            methodNameNode.setNormalizedToken("METHOD_NAME")

            // Retrieve paths from every node individually
            val paths = miner.retrievePaths(methodNode)
            // Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodNode.getMethodInfo())}"
            storage.store(LabeledPathContexts(label, paths.map { toPathContext(it) }))
        }
    }

    storage.save("out_examples/allJavaMethods")
}