package astminer

import astminer.common.forFilesWithSuffix
import astminer.common.model.FunctionInfo
import astminer.common.model.LabeledResult
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.java.JavaFunctionSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

/**
 * Retrieve paths from Java files using ANTLR parser.
 */
fun antlrJavaMethodPaths() {
    val inputDir = "src/test/resources/examples/"
    val outputDir = "examples_output/antlr_java_method_paths"

    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    File(inputDir).forFilesWithSuffix(".java") { file ->
        val node = JavaParser().parseInputStream(file.inputStream())

        val functions: List<FunctionInfo<AntlrNode>> = JavaFunctionSplitter()
            .splitIntoFunctions(node, file.path)
            .map {
                val parametersStr =
                    it.parameters.joinToString(" | ") { param -> "${param.name} ${param.type}" }
                println("${it.name} ${it.returnType} ${it.enclosingElement?.name} [$parametersStr]")
                it
            }
        functions.forEach {
            storage.store(LabeledResult(it.root, it.name ?: "", file.path))
        }
    }

    storage.close()
}

fun main() {
    antlrJavaMethodPaths()
}
