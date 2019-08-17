package astminer.parse.cpp

import astminer.common.Parser
import gremlin.scala.Key
import io.shiftleft.codepropertygraph.Cpg
import io.shiftleft.codepropertygraph.generated.EdgeTypes
import io.shiftleft.codepropertygraph.generated.NodeKeys
import io.shiftleft.codepropertygraph.generated.NodeTypes
import io.shiftleft.fuzzyc2cpg.FuzzyC2Cpg
import io.shiftleft.fuzzyc2cpg.SourceFiles
import io.shiftleft.fuzzyc2cpg.output.inmemory.OutputModuleFactory
import org.apache.commons.io.FileUtils
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Element
import org.apache.tinkerpop.gremlin.structure.Vertex
import java.io.File
import java.io.InputStream
import java.lang.StringBuilder


class FuzzyCppParser : Parser<FuzzyNode> {
    var preprocDirName = "preproc"
        private set

    /**
     * Parse input stream and create AST.
     * If you already have file with code you need to parse, better use [this method][astminer.parse.cpp.FuzzyCppParser.parse],
     * otherwise temporary file for input stream will be created because of fuzzyc2cpg API.
     * @param content to parse
     * @return root of AST if content was parsed, null otherwise
     */
    override fun parse(content: InputStream) : FuzzyNode? {
        val file = File.createTempFile("fuzzy", ".cpp")
        file.deleteOnExit()
        FileUtils.copyInputStreamToFile(content, file)
        val nodes = parse(arrayListOf(file.canonicalPath))
        return if (nodes.size == 1) {
            nodes[0]
        } else {
            null
        }
    }

    /**
     * Parse code from all C/C++ files, found in given paths, and create AST for each file.
     * @param paths in which all C/C++ files are determined; it may contain paths to both files and folders
     * @return list of AST roots for each C/C++ file
     */
    fun parse(paths: List<String>) : List<FuzzyNode?> {
        val outputModuleFactory = OutputModuleFactory()
        FuzzyC2Cpg(outputModuleFactory).runAndOutput(paths.toTypedArray())
        val cpg = outputModuleFactory.internalGraph
        return cpg2nodes(cpg)
    }

    /**
     * Convert [cpg][io.shiftleft.codepropertygraph.Cpg] created by fuzzyc2cpg to list of [FuzzyNode][astminer.parse.cpp.FuzzyNode].
     * Cpg can contain graphs for several files, in that case several AST will be created.
     * @param cpg to be converted
     * @return list of AST roots
     */
    fun cpg2nodes(cpg: Cpg) : List<FuzzyNode?> {
        val g = cpg.graph().traversal()
        val vertexToNode = HashMap<Vertex, FuzzyNode>()
        g.E().hasLabel(EdgeTypes.AST).forEach { addNodesFromEdge(it, vertexToNode) }
        return g.V().hasLabel(NodeTypes.FILE).toList().map { vertexToNode[it] }
    }


    /**
     * Run gcc preprocessor on a given file excluding 'include' directives.
     * The result of preprocessing is stored in created directory named [outputDirName]
     * @param file to preprocess
     */
    fun preprocessWithoutIncludes(file: File, outputDirName: String = preprocDirName) {
        val scriptPath = "${File(System.getProperty("user.dir")).absolutePath}/scripts/fuzzy/convert.sh"
        "chmod +x $scriptPath".runCommand(file.absoluteFile.parentFile)
        "$scriptPath ${file.name} $outputDirName".runCommand(file.absoluteFile.parentFile)
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