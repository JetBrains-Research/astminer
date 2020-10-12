package astminer.examples

import astminer.common.numberOfLines
import astminer.featureextraction.*
import astminer.parse.java.GumTreeJavaParser
import java.io.File


fun parseAndCollectFeatures() {
    val parser = GumTreeJavaParser()
    val features : List<TreeFeature<Any>> = listOf(Depth, NumberOfNodes, BranchingFactor, CompressiblePathLengths, Tokens, NodeTypes)

    val folderInput = "./testData/featureextraction"
    val folderOutput = "out_examples/featureextraction"

    val storage = TreeFeatureValueStorage(",")
    storage.storeFeatures(features)

    File(folderInput).forFilesWithSuffix("java") { fileInput ->
        val fileName = fileInput.name
        val nol = numberOfLines(fileInput)

        val tree = ParsedTree(parser.className(), parser.parseInputStream(fileInput.inputStream()) ?: return@forFilesWithSuffix, fileName, nol)
        storage.storeParsedTree(tree)
    }

    storage.save(folderOutput)
}

fun main() {
    parseAndCollectFeatures()
}