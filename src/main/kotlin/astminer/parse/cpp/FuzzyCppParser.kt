package astminer.parse.cpp

import astminer.common.model.ParseResult
import astminer.common.model.Parser
import gremlin.scala.Key
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.EdgeTypes
import io.shiftleft.codepropertygraph.generated.NodeKeys
import io.shiftleft.codepropertygraph.generated.NodeTypes
import io.shiftleft.fuzzyc2cpg.FuzzyC2Cpg
import io.shiftleft.fuzzyc2cpg.output.inmemory.OutputModuleFactory
import org.apache.commons.io.FileUtils
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.io.File
import java.io.InputStream

/**
 * Parser of C/C++ files based on [FuzzyC2Cpg].
 * By default, it assumes that files have been preprocessed and skips all macroses.
 */
class FuzzyCppParser : Parser<FuzzyNode> {

    companion object {
        private val supportedExtensions = listOf("c", "cpp")

        data class ExpandableNodeKey(
                val key: String,
                val supportedNodeLabels: List<String>,
                val order: Int
        )

        private val expandableNodeKeys = listOf(
                ExpandableNodeKey("NAME", listOf(
                        NodeTypes.TYPE, NodeTypes.TYPE_DECL, NodeTypes.TYPE_PARAMETER, NodeTypes.MEMBER, NodeTypes.TYPE_ARGUMENT,
                        NodeTypes.METHOD, NodeTypes.METHOD_PARAMETER_IN, NodeTypes.LOCAL, NodeTypes.MODIFIER,
                        NodeTypes.IDENTIFIER, NodeTypes.CALL,
                        NodeTypes.UNKNOWN
                ), 0),
                ExpandableNodeKey("TYPE_FULL_NAME", listOf(
                        NodeTypes.TYPE,
                        NodeTypes.METHOD_RETURN, NodeTypes.METHOD_PARAMETER_IN, NodeTypes.LOCAL,
                        NodeTypes.IDENTIFIER,
                        NodeTypes.UNKNOWN
                ), 0),
                ExpandableNodeKey("ALIAS_TYPE_FULL_NAME", listOf(
                        NodeTypes.TYPE_DECL,
                        NodeTypes.UNKNOWN
                ), 0)
        )

        data class ReplaceableNodeKey(val key: String, val condition: (Vertex) -> Boolean)

        private val replaceableNodeKeys = listOf(
                ReplaceableNodeKey("NAME") { v ->
                    v.keys().contains("NAME") &&
                            v.value<String>("NAME").startsWith("<operator>")
                },
                ReplaceableNodeKey("PARSER_TYPE_NAME") { v ->
                    v.keys().contains("PARSER_TYPE_NAME")
                }
        )
    }

    /**
     * Parse input stream and create an AST.
     * If you already have a file with code you need to parse, better use [parseProject] or [parse],
     * otherwise temporary file for input stream will be created because of fuzzyc2cpg API.
     * @param content to parse
     * @return root of AST if content was parsed, null otherwise
     */
    override fun parse(content: InputStream): FuzzyNode? {
        val file = File.createTempFile("fuzzy", ".cpp")
        file.deleteOnExit()
        FileUtils.copyInputStreamToFile(content, file)
        val nodes = parse(listOf(file))
        return nodes[0].root
    }

    /**
     * @see [Parser.parse]
     */
    override fun parse(files: List<File>): List<ParseResult<FuzzyNode>> {
        val outputModuleFactory = OutputModuleFactory()
        val paths = files.map { it.path }
        FuzzyC2Cpg(outputModuleFactory).runAndOutput(paths.toTypedArray())
        val cpg = outputModuleFactory.internalGraph
        return cpg2Nodes(cpg)
    }

    /**
     * Convert [cpg][io.shiftleft.codepropertygraph.Cpg] created by fuzzyc2cpg
     * to list of [FuzzyNode][astminer.parse.cpp.FuzzyNode].
     * Cpg may contain graphs for several files, in that case several ASTs will be created.
     * @param cpg to be converted
     * @return list of AST roots
     */
    private fun cpg2Nodes(cpg: Cpg): List<ParseResult<FuzzyNode>> {
        val g = cpg.graph().traversal()
        val vertexToNode = HashMap<Vertex, FuzzyNode>()
        g.E().hasLabel(EdgeTypes.AST).forEach { addNodesFromEdge(it, vertexToNode) }
        return g.V().hasLabel(NodeTypes.FILE).toList().map { ParseResult(vertexToNode[it], it.value("NAME")) }
    }

    /**
     * Run g++ preprocessor (if [preprocessCommand] is set) on a given file excluding 'include' directives.
     * The result of preprocessing is stored in created directory [outputDir]
     * @param file file to preprocess
     * @param outputDir directory where the preprocessed file will be stored
     * @param preprocessCommand bash command that runs preprocessing, "g++ -E" by default
     */
    fun preprocessFile(file: File, outputDir: File, preprocessCommand: String = "g++ -E") {
        outputDir.mkdirs()
        preprocessCppCode(file, outputDir, preprocessCommand).runCommand(file.absoluteFile.parentFile)
    }

    /**
     * Run preprocessing for all .c and .cpp files in the [project][projectRoot].
     * The preprocessed files will be stored in [outputDir], replicating file hierarchy of the original project.
     * @param projectRoot root of the project that should be preprocessed
     * @param outputDir directory where the preprocessed files will be stored
     */
    fun preprocessProject(projectRoot: File, outputDir: File) {
        val files = projectRoot.walkTopDown()
                .filter { file -> supportedExtensions.contains(file.extension) }
        files.forEach { file ->
            val relativeFilePath = file.relativeTo(projectRoot)
            val outputPath = outputDir.resolve(relativeFilePath.parent)
            outputPath.mkdirs()
            preprocessFile(file, outputPath)
        }
    }

    /**
     * Create string from element with its label and all its properties.
     * @param e - element for converting to string
     * @return created string
     */
    fun elementToString(e: Element) = with(StringBuilder()) {
        append("${e.label()}  |  ")
        e.keys().forEach { k -> append("$k:${e.value<Any>(k)}  ") }
        appendln()
        toString()
    }

    private fun addNodesFromEdge(e: Edge, map: HashMap<Vertex, FuzzyNode>) {
        val parentNode = map.getOrPut(e.outVertex()) { createNodeFromVertex(e.outVertex()) }
        val childNode = map.getOrPut(e.inVertex()) { createNodeFromVertex(e.inVertex()) }
        parentNode.addChild(childNode)
    }

    private fun createNodeFromVertex(v: Vertex): FuzzyNode {
        val token: String? = v.getValueOrNull(NodeKeys.CODE)
        val order: Int? = v.getValueOrNull(NodeKeys.ORDER)

        for (replaceableNodeKey in replaceableNodeKeys) {
            if (replaceableNodeKey.condition(v)) {
                val node = FuzzyNode(v.value<String>(replaceableNodeKey.key), token, order)
                v.keys().forEach { k ->
                    node.setMetadata(k, v.value(k))
                }
                return node
            }
        }

        val node = FuzzyNode(v.label(), token, order)
        v.keys().forEach { k ->
            for (expandableNodeKey in expandableNodeKeys) {
                if (expandableNodeKey.key == k && expandableNodeKey.supportedNodeLabels.contains(v.label())) {
                    val keyNode = FuzzyNode(k, v.value<Any>(k).toString(), expandableNodeKey.order)
                    node.addChild(keyNode)
                    return@forEach
                }
            }
            node.setMetadata(k, v.value(k))
        }
        return node
    }

    private fun <T> Vertex.getValueOrNull(key: Key<out Any>): T? {
        return try {
            this.value<T>(key.name())
        } catch (e: IllegalStateException) {
            null
        }
    }
}
