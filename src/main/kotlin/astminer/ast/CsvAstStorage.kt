package astminer.ast

import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.storage.*
import java.io.File
import java.io.PrintWriter

/**
 * Stores multiple ASTs by their roots and saves them in .csv format.
 * Output consists of 3 .csv files: with node types, with tokens and with ASTs.
 */
class CsvAstStorage(override val directoryPath: String) : AstStorage {

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val nodeTypesMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()

    private val astsOutputStream: PrintWriter

    init {
        File(directoryPath).mkdirs()
        val astsFile = File("$directoryPath/asts.csv")
        astsFile.createNewFile()
        astsOutputStream = PrintWriter(astsFile)
        astsOutputStream.write("id,ast\n")
    }

    override fun store(root: Node, label: String, filePath: String) {
        for (node in root.preOrder()) {
            tokensMap.record(node.getToken())
            nodeTypesMap.record(node.getTypeLabel())
        }
        dumpAst(root, label)
    }

    override fun close() {
        dumpTokenStorage(File("$directoryPath/tokens.csv"))
        dumpNodeTypesStorage(File("$directoryPath/node_types.csv"))

        astsOutputStream.close()
    }

    private fun dumpTokenStorage(file: File) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(nodeTypesMap, "node_type", nodeTypeToCsvString, file)
    }

    private fun dumpAst(root: Node, id: String) {
        astsOutputStream.write("$id,${astString(root)}\n")
    }

    internal fun astString(node: Node): String {
        return "${tokensMap.getId(node.getToken())} ${nodeTypesMap.getId(node.getTypeLabel())}{${
        node.getChildren().joinToString(separator = "", transform = ::astString)
        }}"
    }
}