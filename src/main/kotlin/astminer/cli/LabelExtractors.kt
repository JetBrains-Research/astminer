package astminer.cli

import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.common.model.FunctionInfo
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.java.JavaFunctionSplitter
import astminer.parse.antlr.javascript.JavaScriptFunctionSplitter
import astminer.parse.antlr.python.PythonFunctionSplitter
import astminer.parse.fuzzy.cpp.FuzzyFunctionSplitter
import astminer.parse.fuzzy.cpp.FuzzyNode
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.java.GumTreeJavaFunctionSplitter
import astminer.parse.gumtree.python.GumTreePythonFunctionSplitter
import java.io.File


/**
 * An AST subtree with a label and the path of the source file.
 * @property root The root of the AST subtree.
 * @property label Any label for this subtree.
 * @property filePath The path to the source file where the AST is from.
 */
data class LabeledResult<T : Node>(val root: T, val label: String, val filePath: String)


interface LabelExtractor {
    fun toLabeledData(parseResult: ParseResult<out Node>): List<LabeledResult<out Node>>
}

abstract class FileLabelExtractor : LabelExtractor {

    override fun toLabeledData(
            parseResult: ParseResult<out Node>
    ): List<LabeledResult<out Node>> {
        val (root, filePath) = parseResult
        val label = extractLabel(root, filePath) ?: return emptyList()
        return listOf(LabeledResult(root, label, parseResult.filePath))
    }

    abstract fun extractLabel(root: Node, filePath: String): String?
}

abstract class MethodLabelExtractor(
        open val filterPredicates: Collection<MethodFilter> = emptyList(),
        open val javaParser: String = "gumtree",
        open val pythonParser: String = "antlr"
) : LabelExtractor {

    override fun toLabeledData(
            parseResult: ParseResult<out Node>
    ): List<LabeledResult<out Node>> {
        val (root, filePath) = parseResult
        val fileExtension = File(filePath).extension
        val functionInfos = when (fileExtension) {
            "c", "cpp" -> {
                val functionSplitter = FuzzyFunctionSplitter()
                functionSplitter.splitIntoFunctions(root as FuzzyNode)
            }
            "java" -> {
                when (javaParser) {
                    "gumtree" -> {
                        val methodSplitter = GumTreeJavaFunctionSplitter()
                        methodSplitter.splitIntoFunctions(root as GumTreeNode)
                    }
                    "antlr" -> {
                        val methodSplitter = JavaFunctionSplitter()
                        methodSplitter.splitIntoFunctions(root as AntlrNode)
                    }
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser $javaParser")
                    }
                }
            }
            "py" -> {
                when (pythonParser) {
                    "gumtree" -> {
                        val functionSplitter = GumTreePythonFunctionSplitter()
                        functionSplitter.splitIntoFunctions(root as GumTreeNode)
                    }
                    "antlr" -> {
                        val functionSplitter = PythonFunctionSplitter()
                        functionSplitter.splitIntoFunctions(root as AntlrNode)
                    }
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser $pythonParser")
                    }
                }
            }
            "js" -> {
                val functionSplitter = JavaScriptFunctionSplitter()
                functionSplitter.splitIntoFunctions(root as AntlrNode)
            }
            else -> throw UnsupportedOperationException("Unsupported extension $fileExtension")
        }.filter { functionInfo ->
            filterPredicates.all { predicate ->
                predicate.isFiltered(functionInfo)
            }
        }
        return functionInfos.mapNotNull {
            val label = extractLabel(it, filePath) ?: return@mapNotNull null
            LabeledResult(it.root, label, filePath)
        }
    }

    abstract fun <T : Node> extractLabel(functionInfo: FunctionInfo<T>, filePath: String): String?
}

class FilePathExtractor : FileLabelExtractor() {
    override fun extractLabel(root: Node, filePath: String): String {
        return filePath
    }
}

class FolderExtractor : FileLabelExtractor() {
    override fun extractLabel(root: Node, filePath: String): String? {
        return File(filePath).parentFile.name
    }
}

class MethodNameExtractor(
        override val filterPredicates: Collection<MethodFilter> = emptyList(),
        override val javaParser: String = "gumtree",
        override val pythonParser: String = "antlr"
) : MethodLabelExtractor(filterPredicates, javaParser, pythonParser) {

    override fun <T : Node> extractLabel(functionInfo: FunctionInfo<T>, filePath: String): String? {
        val normalizedName = functionInfo.nameNode?.normalizedToken
        functionInfo.name ?: return null

        functionInfo.root.preOrder().forEach { node ->
            if (node.originalToken == functionInfo.nameNode?.originalToken) node.technicalToken = SELF_CALL_TOKEN
        }
        functionInfo.nameNode?.technicalToken = METHOD_NAME_TOKEN
        return normalizedName
    }

    companion object {
        const val METHOD_NAME_TOKEN = "METHOD_NAME"
        const val SELF_CALL_TOKEN = "SELF"
    }
}
