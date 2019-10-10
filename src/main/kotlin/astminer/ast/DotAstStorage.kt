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
 * Output consist separate .dot files for each AST and one full description in .csv format
 */
class DotAstStorage : AstStorage {

    private val rootsPerEntity: MutableList<Pair<String, Node>> = mutableListOf()

    override fun store(root: Node, label: String) {
        rootsPerEntity.add(Pair(label, root))
    }

    override fun save(directoryPath: String) {
        File(directoryPath).mkdirs()
        val astDirectoryPath = File(directoryPath, "asts")
        astDirectoryPath.mkdirs()

        val descriptionLines = mutableListOf("dot_file,project_path,node_id,token,type")
        val astFilenameFormat = "ast_%d.dot"

        rootsPerEntity.forEachIndexed { index, (label, root) ->
            val nodesMap = dumpAst(root, File(astDirectoryPath, astFilenameFormat.format(index)), File(label).name)
            val nodeDescriptionFormat = "${astFilenameFormat.format(index)},$label,%d,%s,%s"
            for (node in root.preOrder()) {
                node.setNormalizedToken()
                descriptionLines.add(
                        nodeDescriptionFormat.format(nodesMap.getId(node) - 1, node.getNormalizedToken(), node.getTypeLabel())
                )
            }
        }
        writeLinesToFile(descriptionLines, File(directoryPath, "description.csv"))
    }

    private fun dumpAst(root: Node, file: File, astName: String) : RankedIncrementalIdStorage<Node> {
        val nodesMap = RankedIncrementalIdStorage<Node>()
        val astLines = mutableListOf("graph $astName {")

        for (node in root.preOrder()) {
            val childrenIds = node.getChildren().map { nodesMap.record(it) - 1 }
            astLines.add(
                    "${nodesMap.record(node) - 1} -- {${childrenIds.joinToString(" ") { it.toString() }}};"
            )
        }

        astLines.add("}")
        writeLinesToFile(astLines, file)
        return nodesMap
    }

}