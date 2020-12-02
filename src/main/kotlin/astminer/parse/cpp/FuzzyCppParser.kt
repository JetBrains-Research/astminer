package astminer.parse.cpp

import astminer.common.model.ParseResult
import astminer.common.model.Parser
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.EdgeTypes
import io.shiftleft.codepropertygraph.generated.NodeKeys
import io.shiftleft.codepropertygraph.generated.NodeTypes
import io.shiftleft.fuzzyc2cpg.FuzzyC2Cpg
import overflowdb.Edge
import overflowdb.Node
import overflowdb.Element
import scala.Option
import scala.collection.immutable.Set
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

        data class ReplaceableNodeKey(val key: String, val condition: (Node) -> Boolean)

        private val replaceableNodeKeys = listOf(
                ReplaceableNodeKey("NAME") { v ->
                    v.propertyKeys().contains("NAME") &&
                            v.property("NAME").toString().startsWith("<operator>")
                },
                ReplaceableNodeKey("PARSER_TYPE_NAME") { v ->
                    v.propertyKeys().contains("PARSER_TYPE_NAME")
                }
        )
    }

    /**
     * Parse input stream and create an AST.
     * If you already have a file with code you need to parse, better use [parseFile],
     * otherwise temporary file for input stream will be created because of fuzzyc2cpg API.
     * @param content to parse
     * @return root of AST if content was parsed, null otherwise
     */
    override fun parseInputStream(content: InputStream): FuzzyNode? {
        val file = File.createTempFile("fuzzy", ".cpp")
        file.deleteOnExit()
        file.outputStream().use {
            content.copyTo(it)
        }
        val nodes = parseFile(file)
        return nodes.root
    }

    /**
     * Parse a single file and create an AST.
     * @param file to parse
     * @return [ParseResult] with root of an AST (null if parsing failed) and file path
     */
    override fun parseFile(file: File): ParseResult<FuzzyNode> {
        // We need some tweaks to create Scala sets from Kotlin code
        val pathSetScalaBuilder = Set.newBuilder<String>()
        pathSetScalaBuilder.addOne(file.path)
        val pathSet = pathSetScalaBuilder.result()
        val extensionSetScalaBuilder = Set.newBuilder<String>()
        extensionSetScalaBuilder.addOne(".${file.extension}")
        val extensionSet = extensionSetScalaBuilder.result()

        // Kotlin cannot use default value Scala:None for the argument, so we create it manually
        val optionalOutputPath: Option<String> = Option.empty()

        val cpg = FuzzyC2Cpg().runAndOutput(pathSet, extensionSet, optionalOutputPath)
        return cpg2Nodes(cpg, file.path)
    }

    /**
     * Convert [cpg][io.shiftleft.codepropertygraph.Cpg] created by fuzzyc2cpg
     * to list of [FuzzyNode][astminer.parse.cpp.FuzzyNode].
     * Cpg may contain graphs for several files, in that case several ASTs will be created.
     * @param cpg to be converted
     * @param filePath to the parsed file that will be used if parsing failed
     * @return list of AST roots
     */
    private fun cpg2Nodes(cpg: Cpg, filePath: String): ParseResult<FuzzyNode> {
        val g = cpg.graph()
        val vertexToNode = mutableMapOf<Node, FuzzyNode>()
        g.E().forEach {
            if (it.label() == EdgeTypes.AST) {
                addNodesFromEdge(it, vertexToNode)
            }
        }
        g.V().forEach {
            if (it.label() == NodeTypes.FILE) {
                val actualFilePath = it.property("NAME").toString()
                if (File(actualFilePath).absolutePath != File(filePath).absolutePath) {
                    println("While parsing $filePath, actually parsed $actualFilePath")
                }
                return ParseResult(vertexToNode[it], actualFilePath)
            }
        }
        return ParseResult(null, filePath)
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
            val outputPath = if (relativeFilePath.parent != null){
                outputDir.resolve(relativeFilePath.parent)
            } else {
                outputDir
            }
            outputPath.mkdirs()
            preprocessFile(file, outputPath)
        }
    }

    private fun addNodesFromEdge(e: Edge, map: MutableMap<Node, FuzzyNode>) {
        val parentNode = map.getOrPut(e.outNode()) { createNodeFromVertex(e.outNode()) }
        val childNode = map.getOrPut(e.inNode()) { createNodeFromVertex(e.inNode()) }
        parentNode.addChild(childNode)
    }

    private fun createNodeFromVertex(v: Node): FuzzyNode {
        val token: String? = v.property(NodeKeys.CODE)
        val order: Int? = v.property(NodeKeys.ORDER)

        for (replaceableNodeKey in replaceableNodeKeys) {
            if (replaceableNodeKey.condition(v)) {
                val node = FuzzyNode(v.property(replaceableNodeKey.key).toString(), token, order)
                v.propertyKeys().forEach { k ->
                    val property = v.property(k) ?: return@forEach
                    node.setMetadata(k, property.toString())
                }
                return node
            }
        }

        val node = FuzzyNode(v.label(), token, order)
        v.propertyKeys().forEach { k ->
            val property = v.property(k)?.toString() ?: return@forEach
            for (expandableNodeKey in expandableNodeKeys) {
                if (expandableNodeKey.key == k && expandableNodeKey.supportedNodeLabels.contains(v.label())) {
                    val keyNode = FuzzyNode(k, property, expandableNodeKey.order)
                    node.addChild(keyNode)
                    return@forEach
                }
            }
            node.setMetadata(k, property)
        }
        return node
    }
}
