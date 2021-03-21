package astminer.examples

import astminer.common.model.LabeledPathContexts
import astminer.common.model.MethodInfo
import astminer.parse.python.GumTreePythonMethodSplitter
import astminer.parse.python.GumTreePythonNode
import astminer.parse.python.GumTreePythonParser
import astminer.paths.CsvPathStorage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import java.io.File

private fun getCsvFriendlyMethodId(methodInfo: MethodInfo<GumTreePythonNode>): String {
    val className = methodInfo.enclosingElementName() ?: ""
    val methodName = methodInfo.name() ?: "unknown_method"
    val parameterTypes = methodInfo.methodParameters.joinToString("|") { it.name() ?: "_" }
    return "$className.$methodName($parameterTypes)"
}

fun allPythonMethods() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter"

    val miner = PathMiner(PathRetrievalSettings(5, 5))
    val outputDir = "out_examples/allPythonMethods"
    val storage = CsvPathStorage(outputDir)

    File(inputDir).forFilesWithSuffix(".py") { file ->
        // parse file
        val fileNode = GumTreePythonParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix

        // extract method nodes
        val methodNodes = GumTreePythonMethodSplitter().splitIntoMethods(fileNode)

        methodNodes.forEach { methodInfo ->
            // Retrieve paths from every node individually
            val paths = miner.retrievePaths(methodInfo.method.root)
            // Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodInfo)}"
            storage.store(LabeledPathContexts(entityId, paths.map { toPathContext(it) }))
        }
    }

    storage.close()
}
