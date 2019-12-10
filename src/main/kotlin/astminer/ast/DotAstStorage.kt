package astminer.ast

import astminer.common.getNormalizedToken
import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.common.storage.RankedIncrementalIdStorage
import astminer.common.storage.writeLinesToFile
import java.io.File

/**
 * Stores multiple ASTs in dot format (https://en.wikipedia.org/wiki/DOT_(graph_description_language))
 * Output consist of separate .dot files for each AST and one full description in .csv format
 */
class DotAstStorage : AstStorage {

    private data class Ast(val label: String, val root: Node)

    private val rootsPerEntity: MutableList<Ast> = mutableListOf()

    override fun store(root: Node, label: String) {
        rootsPerEntity.add(Ast(label, root))
    }

    override fun save(directoryPath: String) {
        File(directoryPath).mkdirs()
        val astDirectoryPath = File(directoryPath, "asts")
        astDirectoryPath.mkdirs()

        val descriptionLines = mutableListOf("dot_file,source_file,label,node_id,token,type")
        val astFilenameFormat = "ast_%d.dot"

        rootsPerEntity.forEachIndexed { index, (label, root) ->
            val labelAsFile = File(label)
            val sourceFile = labelAsFile.parentFile.path
            val normalizedLabel = normalizeAstLabel(labelAsFile.name)
            val nodesMap = dumpAst(root, File(astDirectoryPath, astFilenameFormat.format(index)), normalizedLabel)
            val nodeDescriptionFormat = "${astFilenameFormat.format(index)},$sourceFile,$label,%d,%s,%s"
            for (node in root.preOrder()) {
                descriptionLines.add(
                        nodeDescriptionFormat.format(nodesMap.getId(node) - 1, node.getNormalizedToken(), node.getTypeLabel())
                )
            }
        }
        writeLinesToFile(descriptionLines, File(directoryPath, "description.csv"))
    }

    private fun dumpAst(root: Node, file: File, astName: String) : RankedIncrementalIdStorage<Node> {
        val nodesMap = RankedIncrementalIdStorage<Node>()
        val astLines = mutableListOf("digraph $astName {")

        for (node in root.preOrder()) {
            val rootId = nodesMap.record(node) - 1
            val childrenIds = node.getChildren().map { nodesMap.record(it) - 1 }
            astLines.add(
                    "$rootId -- {${childrenIds.joinToString(" ") { it.toString() }}};"
            )
        }

        astLines.add("}")
        writeLinesToFile(astLines, file)
        return nodesMap
    }

    // label should contain only latin letters and underscores, other symbols replace with an underscore
    internal fun normalizeAstLabel(label: String): String =
            label.replace("[^A-z,^_]".toRegex(), "_")

}