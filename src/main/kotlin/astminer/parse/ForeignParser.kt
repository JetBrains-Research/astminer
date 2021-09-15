package astminer.parse

import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.config.FileExtension
import astminer.config.ParserType
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.InputStream
import kotlin.io.path.createTempDirectory

fun getTreeFromScript(args: List<String>): SimpleNode {
    val treeString = launchScript(args)
    val foreignTree = Json.decodeFromString<ForeignTree>(treeString)
    return convertFromForeignTree(foreignTree)
}

private fun launchScript(args: List<String>): String {
    val processBuilder = ProcessBuilder(args)
    processBuilder.redirectErrorStream(true)
    val process = processBuilder.start()
    val treeString = process.inputStream.bufferedReader().use { it.readText() }
    process.waitFor()

    return treeString
}

private fun convertFromForeignTree(context: ForeignTree, rootId: Int = 0, parent: SimpleNode? = null): SimpleNode {
    val foreignNode = context.tree[rootId]
    val node = SimpleNode(foreignNode.nodeType, mutableListOf(), parent, foreignNode.token)
    val children = foreignNode.children.map { convertFromForeignTree(context, it, node) }
    node.children.addAll(children)
    return node
}

@Serializable
data class ForeignTree(val tree: List<ForeignNode>)

@Serializable
data class ForeignNode(val token: String?, val nodeType: String, val children: List<Int>)

class SimpleNode(
    override val typeLabel: String,
    override val children: MutableList<SimpleNode>,
    override val parent: Node?,
    token: String?
) : Node(token) {
    override fun removeChildrenOfType(typeLabel: String) {
        children.removeIf { it.typeLabel == typeLabel }
    }

    override fun getChildOfType(typeLabel: String) = super.getChildOfType(typeLabel) as? SimpleNode
    override fun preOrder() = super.preOrder().map { it as SimpleNode }
}

abstract class ForeignParser : Parser<SimpleNode> {
    abstract val parser: ParserType
    abstract val language: FileExtension

    private val tempPath by lazy {
        createTempDirectory(prefix = "${parser.name}Tmp").also { it.toFile().deleteOnExit() }
    }
    private val suffix by lazy { ".${language.fileExtension}" }

    abstract fun getArguments(file: File): List<String>

    override fun parseInputStream(content: InputStream): SimpleNode {
        val tempFile = kotlin.io.path.createTempFile(suffix = suffix, directory = tempPath).toFile()
        FileUtils.copyInputStreamToFile(content, tempFile)

        return try {
            parseFile(tempFile)
        } catch (e: SerializationException) {
            throw ParsingException(parserType = parser.name, language = language.name, e)
        } finally {
            tempFile.delete()
        }
    }

    override fun parseFile(file: File): SimpleNode {
        return try {
            getTreeFromScript(getArguments(file))
        } catch (e: SerializationException) {
            throw ParsingException(parserType = parser.name, language = language.name, e)
        }
    }
}
