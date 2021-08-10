package astminer.storage.ast

import astminer.common.model.DatasetHoldout
import astminer.common.model.LabeledResult
import astminer.common.model.Node
import astminer.common.model.Storage
import astminer.common.storage.RankedIncrementalIdStorage
import astminer.common.storage.dumpIdStorageToCsv
import astminer.common.storage.nodeTypeToCsvString
import astminer.common.storage.tokenToCsvString
import java.io.File
import java.io.PrintWriter

/**
 * Stores multiple ASTs by their roots and saves them in .csv format.
 * Output consists of 3 .csv files: with node types, with tokens and with ASTs.
 */
class CsvAstStorage(override val outputDirectoryPath: String) : Storage {

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val nodeTypesMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()

    private val astsPrintWriters = mutableMapOf<DatasetHoldout, PrintWriter>()

    init {
        File(outputDirectoryPath).mkdirs()
    }

    override fun store(labeledResult: LabeledResult<out Node>, holdout: DatasetHoldout) {
        for (node in labeledResult.root.preOrder()) {
            tokensMap.record(node.token)
            nodeTypesMap.record(node.typeLabel)
        }
        val writer = astsPrintWriters.getOrPut(holdout) { holdout.resolveHoldout() }
        dumpAst(labeledResult.root, labeledResult.label, writer)
    }

    override fun close() {
        dumpTokenStorage(File("$outputDirectoryPath/tokens.csv"))
        dumpNodeTypesStorage(File("$outputDirectoryPath/node_types.csv"))

        astsPrintWriters.values.map { it.close() }
    }

    private fun dumpTokenStorage(file: File) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(nodeTypesMap, "node_type", nodeTypeToCsvString, file)
    }

    private fun dumpAst(root: Node, id: String, writer: PrintWriter) {
        writer.println("$id,${astString(root)}")
    }

    internal fun astString(node: Node): String {
        return "${tokensMap.getId(node.token)} ${nodeTypesMap.getId(node.typeLabel)}{${
        node.children.joinToString(separator = "", transform = ::astString)
        }}"
    }

    private fun DatasetHoldout.resolveHoldout(): PrintWriter {
        val holdoutDir = File(outputDirectoryPath).resolve(this.dirName)
        holdoutDir.mkdirs()
        val astFile = holdoutDir.resolve("asts.csv")
        astFile.createNewFile()
        val newWriter = PrintWriter(astFile)
        newWriter.println("id,ast")
        return newWriter
    }
}
