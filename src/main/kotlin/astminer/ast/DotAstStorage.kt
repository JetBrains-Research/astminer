package astminer.ast

import astminer.common.getNormalizedToken
import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.common.storage.RankedIncrementalIdStorage
import astminer.common.storage.writeLinesToFile
import java.io.File

/**
 * Stores multiple ASTs in dot format (https://en.wikipedia.org/wiki/DOT_(graph_description_language))
 * Output consist of separate .dot files for each AST and one full description in .csv format
 */
class DotAstStorage : AstStorage {

    private data class Ast(val label: String, val root: Node)
    internal data class FilePath(val parentPath: String, val fileName: String)

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

        rootsPerEntity.forEachIndexed { index, (fullPath, root) ->
            // Use filename as a label for ast
            // TODO: save full signature for method
            val (sourceFile, label) = splitFullPath(fullPath)
            val normalizedLabel = normalizeAstLabel(label)
            val normalizedFilepath = normalizeFilepath(sourceFile)
            val nodesMap = dumpAst(root, File(astDirectoryPath, astFilenameFormat.format(index)), normalizedLabel)
            val nodeDescriptionFormat = "${astFilenameFormat.format(index)},$normalizedFilepath,$label,%d,%s,%s"
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

    // Label should contain only latin letters and underscores, other symbols replace with an underscore
    internal fun normalizeAstLabel(label: String): String =
            label.replace("[^A-z^_]".toRegex(), "_")

    /**
     * Filepath should contain only latin letters, underscores, hyphens, backslashes and dots
     * Underscore replace other symbols
     */
    internal fun normalizeFilepath(filepath: String): String =
            filepath.replace("[^A-z^_^\\-^.^/]".toRegex(), "_")

    /**
     * Split the full path to specified file into the parent's path, and the file name
     * In case of single file name returns an empty string for the parent path
     * Example: "/foo/boo/gav" -> FilePath("/foo/boo", "gav"), "gav" -> FilePath("", "gav")
     */
    internal fun splitFullPath(fullPath: String): FilePath {
        val fileObject = File(fullPath)
        return FilePath(fileObject.parentFile?.path ?: "", fileObject.name)
    }

}
