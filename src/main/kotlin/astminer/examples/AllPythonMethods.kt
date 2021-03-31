package astminer.examples

import astminer.common.model.MethodInfo
import astminer.parse.python.GumTreePythonMethodSplitter
import astminer.parse.python.GumTreePythonNode
import astminer.parse.python.GumTreePythonParser
import astminer.storage.CsvPathStorage
import astminer.storage.CountingPathStorageConfig
import astminer.storage.LabellingResult
import java.io.File

private fun getCsvFriendlyMethodId(methodInfo: MethodInfo<GumTreePythonNode>): String {
    val className = methodInfo.enclosingElementName() ?: ""
    val methodName = methodInfo.name() ?: "unknown_method"
    val parameterTypes = methodInfo.methodParameters.joinToString("|") { it.name() ?: "_" }
    return "$className.$methodName($parameterTypes)"
}

fun allPythonMethods() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter"

    val outputDir = "out_examples/allPythonMethods"
    val storage = CsvPathStorage(outputDir, CountingPathStorageConfig(5, 5, false))

    File(inputDir).forFilesWithSuffix(".py") { file ->
        // parse file
        val fileNode = GumTreePythonParser().parseInputStream(file.inputStream()) ?: return@forFilesWithSuffix

        // extract method nodes
        val methodNodes = GumTreePythonMethodSplitter().splitIntoMethods(fileNode)

        methodNodes.forEach { methodInfo ->
            // Retrieve a method identifier
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodInfo)}"
            val labelingResult = LabellingResult(fileNode, entityId, file.path)
            // Retrieve paths from each method individually and store them
            storage.store(labelingResult)
        }
    }

    storage.close()
}
