package astminer.ast

import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.storage.*
import java.io.File
import java.io.OutputStreamWriter

/**
 * Stores multiple ASTs by their roots and saves them in .csv format.
 * Output consists of 3 .csv files: with node types, with tokens and with ASTs.
 */
class CsvAstStorage : AstStorage {

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val nodeTypesMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()

    private lateinit var directoryPath: String
    private lateinit var astsFileWriter: OutputStreamWriter

    fun init(directoryPath: String) {
        this.directoryPath = directoryPath
        astsFileWriter = File("$directoryPath/asts.csv").writer()
        astsFileWriter.write("id,ast\n")
    }

    override fun store(root: Node, label: String) {
        for (node in root.preOrder()) {
            tokensMap.record(node.getToken())
            nodeTypesMap.record(node.getTypeLabel())
        }
        dumpAsts(label, root)
    }

    override fun save(directoryPath: String) {
        File(directoryPath).mkdirs()
        dumpTokenStorage(File("$directoryPath/tokens.csv"))
        dumpNodeTypesStorage(File("$directoryPath/node_types.csv"))
        astsFileWriter.close()
    }

    private fun dumpTokenStorage(file: File) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(nodeTypesMap, "node_type", nodeTypeToCsvString, file)
    }

    private fun dumpAsts(id: String, root: Node) {
        astsFileWriter.write("$id,${astString(root)}\n")
    }

    internal fun astString(node: Node): String {
        return "${tokensMap.getId(node.getToken())} ${nodeTypesMap.getId(node.getTypeLabel())}{${
        node.getChildren().joinToString(separator = "", transform = ::astString)
        }}"
    }
}