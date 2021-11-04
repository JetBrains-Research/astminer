package astminer.parse

import astminer.common.SimpleNode
import astminer.common.model.NodeRange
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

/** Launches external script with java `ProcessBuilder` and
 *  converts output to `astminer` tree.
 *
 *  Output of the script must be a json tree. Example:
 *  ```
 *  {
 *    "tree": [
 *      {
 *        "token": null,
 *        "nodeType": "i_am_root",
 *        "children": [1,2],
 *        "range" : {
 *          "start" : { "l" : 0, "c" : 0 },
 *          "end" : { "l" 1, "c" : 4 }
 *        }
 *      },
 *      {
 *        "token": "Hello",
 *        "nodeType": "left_child",
 *        "children": []
 *        "range" : {
 *          "start" : { "l" : 0, "c": 0 },
 *          "end" : { "l": 0, "c": 5 }
 *        }
 *      },
 *      {
 *        "token": "World!",
 *        "nodeType": "right_child",
 *        "children": [],
 *        "range" : {
 *          "start" : { "l" : 1, "c" : 0 },
 *          "end" : { "l" : 1, "c" : 6 }
 *        }
 *      }
 *    ]
 *  }
 *  ```
 **/
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

    val node = SimpleNode(
        children = mutableListOf(),
        parent = parent,
        typeLabel = foreignNode.nodeType,
        token = foreignNode.token,
        range = foreignNode.range
    )
    val children = foreignNode.children.map { convertFromForeignTree(context, it, node) }
    node.children.addAll(children)
    return node
}

@Serializable
private data class ForeignTree(val tree: List<ForeignNode>)

@Serializable
private data class ForeignNode(
    val token: String?,
    val nodeType: String,
    val range: NodeRange? = null,
    val children: List<Int>
)

/** Use this parser to get a tree from external script.
 *  It uses `getTreeFromScript` and `getArguments` functions to generate
 *  parameters based on target file and then launches java `ProcessBuilder`
 *  on these parameters to get `astminer` tree
 *
 *  Please note that external script must output json AST.
 *  @see getTreeFromScript**/
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
