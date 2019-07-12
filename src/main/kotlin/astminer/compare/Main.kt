package astminer.compare

import astminer.common.Node
import astminer.common.Parser
import astminer.examples.forFilesWithSuffix
import astminer.parse.antlr.java.JavaParser
import astminer.parse.java.GumTreeJavaParser
import java.io.File

fun numberOfLines(file: File) : Int {
    return file.readLines().filter { it != "" }.size
}

fun main() {
    val parsers : List<Parser<out Node>> = listOf(GumTreeJavaParser(), JavaParser())
    val features : List<TreeFeature<out Any>> = listOf(Depth, NumberOfNodes, BranchingFactor, DegeneratedPathLengths)

    val folderInput = "./testData/compare/"
    val folderOutput = "./out_examples/compare"

    val storage = TreeFeatureValueStorage(", ")
    storage.storeFeatures(features)

    File(folderInput).forFilesWithSuffix("java") { fileInput ->

        val fileName = fileInput.name
        val nol = numberOfLines(fileInput)

        val trees = parsers.map { ParsedTree(it.name(), it.parse(fileInput.inputStream()) ?: return@forFilesWithSuffix, fileName, nol)}
        trees.forEach { storage.storeParsedTree(it) }

    }

    storage.save(folderOutput)
}