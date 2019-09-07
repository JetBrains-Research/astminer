package astminer.parse.cpp

import astminer.common.Parser
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
import java.lang.StringBuilder

/**
 * Parser of C/C++ files based of [FuzzyC2Cpg].
 * By default, it assumes that files have been preprocessed and skips all macroses.
 */
class FuzzyCppParser : Parser<FuzzyNode> {

    private val supportedExtensions = listOf("c", "cpp")

    /**
     * Parse input stream and create AST.
     * If you already have file with code you need to parse, better use [parseProject] or [parse],
     * otherwise temporary file for input stream will be created because of fuzzyc2cpg API.
     * @param content to parse
     * @return root of AST if content was parsed, null otherwise
     */
    override fun parse(content: InputStream) : FuzzyNode? {
        val file = File.createTempFile("fuzzy", ".cpp")
        file.deleteOnExit()
        FileUtils.copyInputStreamToFile(content, file)
        val nodes = parse(listOf(file))
        return if (nodes.size == 1) {
            nodes[0]
        } else {
            null
        }
    }

    /**
     * @see [Parser.parse]
     */
    override fun parse(files: List<File>) : List<FuzzyNode?> {
        val outputModuleFactory = OutputModuleFactory()
        val paths = files.map { it.path }
        FuzzyC2Cpg(outputModuleFactory).runAndOutput(paths.toTypedArray())
        val cpg = outputModuleFactory.internalGraph
        return cpg2nodes(cpg)
    }

    /**
     * Convert [cpg][io.shiftleft.codepropertygraph.Cpg] created by fuzzyc2cpg to list of [FuzzyNode][astminer.parse.cpp.FuzzyNode].
     * Cpg may contain graphs for several files, in that case several ASTs will be created.
     * @param cpg to be converted
     * @return list of AST roots
     */
    private fun cpg2nodes(cpg: Cpg) : List<FuzzyNode?> {
        val g = cpg.graph().traversal()
        val vertexToNode = HashMap<Vertex, FuzzyNode>()
        g.E().hasLabel(EdgeTypes.AST).forEach { addNodesFromEdge(it, vertexToNode) }
        return g.V().hasLabel(NodeTypes.FILE).toList().map { vertexToNode[it] }
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
        println(projectRoot.path)
        val files = projectRoot.walkTopDown().filter { file -> supportedExtensions.contains(file.extension) }
        println(files.map {it.path})
        files.forEach { file ->
            val relativeFilePath = file.relativeTo(projectRoot)
            val outputPath = outputDir.resolve(relativeFilePath.parent)
            println(relativeFilePath)
            println(outputPath)
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

    private fun createNodeFromVertex(v: Vertex) : FuzzyNode {
        val token: String? = v.getValueOrNull(NodeKeys.CODE)
        val order: Int? = v.getValueOrNull(NodeKeys.ORDER)
        val node = FuzzyNode(v.label(), token, order)
        v.keys().forEach { k -> node.setMetadata(k, v.value(k)) }
        return node
    }

    private fun <T> Vertex.getValueOrNull(key: Key<out Any>) : T? {
        return try {
            this.value<T>(key.name())
        } catch (e: IllegalStateException) {
            null
        }
    }
}