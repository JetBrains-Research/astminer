package astminer

import astminer.common.forFilesWithSuffix
import astminer.common.numberOfLines
import astminer.featureextraction.*
import astminer.parse.gumtree.java.GumTreeJavaParser
import java.io.File

/**
 * Collect different features/statistics from parsed trees.
 * Target language is Java, using GumTree parser.
 */
fun collectFeatures() {
    val parser = GumTreeJavaParser()
    val features: List<TreeFeature<Any>> =
        listOf(Depth, NumberOfNodes, BranchingFactor, CompressiblePathLengths, Tokens, NodeTypes)

    val inputDir = "src/test/resources/featureextraction"
    val outputDir = "examples_output/collected_features"

    val storage = TreeFeatureValueStorage(",")
    storage.storeFeatures(features)

    File(inputDir).forFilesWithSuffix("java") { fileInput ->
        val fileName = fileInput.name
        val nol = numberOfLines(fileInput)

        val tree = ParsedTree(parser.className(), parser.parseInputStream(fileInput.inputStream()), fileName, nol)
        storage.storeParsedTree(tree)
    }

    storage.save(outputDir)
}

fun main() {
    collectFeatures()
}
