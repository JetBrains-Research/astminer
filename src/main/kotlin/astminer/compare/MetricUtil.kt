package astminer.compare

import astminer.common.Node
import astminer.common.Parser
import astminer.examples.forFilesWithSuffix
import astminer.parse.java.GumTreeJavaParser
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import java.io.File


fun Node.findDepth() : Long {
    var max =  this.getChildren().map { it.findDepth() }.max() ?: 0
    return ++max
}

fun Node.findNumberOfAllNodes() : Long {
    return this.getChildren().map { it.findNumberOfAllNodes() }.sum() + 1
}

fun findBranchingFactorsCount(node : Node, factorCountMap : MutableMap<Int, Long>) : Map<Int, Long> {
    node.getChildren().map { findBranchingFactorsCount(it, factorCountMap) }
    val factor  = node.getChildren().size
    factorCountMap[factor] = (factorCountMap.getOrDefault(factor, 0)) + 1
    return factorCountMap
}

fun findAllTokens(node : Node, tokensList : MutableList<String>) : List<String> {
    node.getChildren().map { findAllTokens(it, tokensList) }
    tokensList.add(node.getToken())
    return tokensList
}

fun findAllTokenTypes(node : Node, tokenTypesList : MutableList<String>) : List<String> {
    node.getChildren().map { findAllTokenTypes(it, tokenTypesList) }
    tokenTypesList.add(node.getTypeLabel())
    return tokenTypesList
}

fun parseFileAndStoreToFile(parser: Parser<out Node>, settings: PathRetrievalSettings, fileInput : File, fileOutput: File ) {
    val miner = PathMiner(settings)

    val tree = parser.parse(fileInput.inputStream()) ?: return
    val paths = miner.retrievePaths(tree)

    saveWithHeader(listOf("${tree.findDepth()}"), "depth", fileOutput)
    saveWithHeader(listOf("${tree.findNumberOfAllNodes()}"), "numberOfAllNodes", fileOutput)
    saveWithHeader(findBranchingFactorsCount(tree, HashMap()).toList().map { it.toString() }, "branchingFactorsCount", fileOutput)
    saveWithHeader(findAllTokens(tree, ArrayList()), "tokens", fileOutput)
    saveWithHeader(findAllTokenTypes(tree, ArrayList()), "tokenTypes", fileOutput)
}


fun saveWithHeader(toSave : List<String>, header : String, file : File) {
    appendLinesToFile(listOf("$header", "${toSave.size}") + toSave, file)
}

fun appendLinesToFile(lines : List<String>, file : File) {
    lines.forEach { file.appendText(it + "\n") }
    file.appendText("\n")
}


fun main() {

    val folderInput = "./testData/compare/"
    val folderOutput = "out_examples/compare"
    File(folderOutput).mkdirs()
    val parser : Parser<out Node> = GumTreeJavaParser()
    val settings = PathRetrievalSettings(5, 5)

    File(folderInput).forFilesWithSuffix(".java") { fileInput ->
        val fileOutput = File("$folderOutput/${fileInput.nameWithoutExtension}.csv")
        parseFileAndStoreToFile(parser, settings, fileInput, fileOutput)
    }

}

