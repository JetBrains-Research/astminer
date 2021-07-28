package astminer

import astminer.common.forFilesWithSuffix
import astminer.common.model.FunctionInfo
import astminer.common.model.LabeledResult
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.java.GumTreeJavaFunctionSplitter
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

private fun getCsvFriendlyMethodId(functionInfo: FunctionInfo<GumTreeNode>): String {
    val className = functionInfo.enclosingElement?.name ?: ""
    val methodName = functionInfo.name
    val parameterTypes = functionInfo.parameters.joinToString("|") { it.name }
    return "$className.$methodName($parameterTypes)"
}

/**
 * Retrieve paths from all Java files, using a GumTree parser.
 * GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
 */
fun gumTreeJavaMethodPaths() {
    val inputDir = "src/test/resources/examples"
    val outputDir = "examples_output/gumtree_java_method_paths"

    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    File(inputDir).forFilesWithSuffix(".java") { file ->
        // parse file
        val fileNode = GumTreeJavaParser().parseInputStream(file.inputStream())

        // extract method nodes
        val methodNodes = GumTreeJavaFunctionSplitter().splitIntoFunctions(fileNode, file.path)

        methodNodes.forEach { methodInfo ->
            // Retrieve a method identifier
            println("Method name: ${methodInfo.name}, " +
                    "modifiers: ${methodInfo.modifiers}, " +
                    "annotations: ${methodInfo.annotations}")
            val entityId = "${file.path}::${getCsvFriendlyMethodId(methodInfo)}"
            val labelingResult = LabeledResult(fileNode, entityId, file.path)
            storage.store(labelingResult)
        }
    }

    storage.close()
}

fun main() {
    gumTreeJavaMethodPaths()
}
