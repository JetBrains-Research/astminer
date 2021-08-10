package astminer.storage.ast

import astminer.common.model.DatasetHoldout
import astminer.common.model.LabeledResult
import astminer.common.model.Node
import astminer.common.model.Storage
import astminer.common.storage.RankedIncrementalIdStorage
import java.io.File
import java.io.PrintWriter

/**
 * Stores multiple ASTs in dot format (https://en.wikipedia.org/wiki/DOT_(graph_description_language))
 * Output consist of separate .dot files for each AST and one full description in .csv format
 */
class DotAstStorage(override val outputDirectoryPath: String) : Storage {

    internal data class FilePath(val parentPath: String, val fileName: String)

    private val astDirectoryPaths = mutableMapOf<DatasetHoldout, File>()
    private val astFilenameFormat = "ast_%d.dot"
    private val descriptionFileStream: PrintWriter
    private var index: Long = 0

    init {
        File(outputDirectoryPath).mkdirs()
        val descriptionFile = File(outputDirectoryPath, "description.csv")
        descriptionFile.createNewFile()
        descriptionFileStream = PrintWriter(descriptionFile)
        descriptionFileStream.write("dot_file,source_file,label,node_id,token,type\n")
    }

    override fun store(labeledResult: LabeledResult<out Node>, holdout: DatasetHoldout) {
        // Use filename as a label for ast
        // TODO: save full signature for method
        val normalizedLabel = normalizeAstLabel(labeledResult.label)
        val normalizedFilepath = normalizeFilepath(labeledResult.filePath)
        val astDirectoryPath = astDirectoryPaths.getOrPut(holdout) { holdout.resolveHoldout() }
        val nodesMap =
            dumpAst(labeledResult.root, File(astDirectoryPath, astFilenameFormat.format(index)), normalizedLabel)
        val nodeDescriptionFormat = "${astFilenameFormat.format(index)},$normalizedFilepath,$normalizedLabel,%d,%s,%s"
        for (node in labeledResult.root.preOrder()) {
            descriptionFileStream.write(
                nodeDescriptionFormat.format(nodesMap.getId(node) - 1, node.token, node.typeLabel) + "\n"
            )
        }
        ++index
    }

    override fun close() {
        descriptionFileStream.close()
    }

    private fun dumpAst(root: Node, file: File, astName: String): RankedIncrementalIdStorage<Node> {
        val nodesMap = RankedIncrementalIdStorage<Node>()
        // dot parsers (e.g. pydot) can't parse graph/digraph if its name is "graph"
        val fixedAstName = if (astName == "graph" || astName == "digraph") "_$astName" else astName

        file.printWriter().use { out ->
            out.println("digraph $fixedAstName {")
            for (node in root.preOrder()) {
                val rootId = nodesMap.record(node) - 1
                val childrenIds = node.children.map { nodesMap.record(it) - 1 }
                out.println("$rootId -- {${childrenIds.joinToString(" ") { it.toString() }}};")
            }

            out.println("}")
        }
        return nodesMap
    }

    private fun DatasetHoldout.resolveHoldout(): File {
        val outputDir = File(outputDirectoryPath)
        val asts = outputDir.resolve(this.dirName).resolve("asts")
        asts.mkdirs()
        return asts
    }

    // Label should contain only latin letters, numbers and underscores, other symbols replace with an underscore
    internal fun normalizeAstLabel(label: String): String =
        label.replace("[^A-z0-9_]".toRegex(), "_")

    /**
     * Filepath should contain only latin letters, numbers, underscores, hyphens, backslashes and dots
     * Underscore replace other symbols
     */
    internal fun normalizeFilepath(filepath: String): String =
        filepath.replace("[^A-z0-9_\\-./]".toRegex(), "_")

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
