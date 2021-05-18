package astminer.cli

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.common.preOrder
import astminer.common.setTechnicalToken
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.javascript.JavaScriptMethodSplitter
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.fuzzy.cpp.FuzzyMethodSplitter
import astminer.parse.fuzzy.cpp.FuzzyNode
import astminer.parse.gumtree.GumTreeNode
import astminer.parse.gumtree.java.GumTreeJavaMethodSplitter
import astminer.parse.gumtree.python.GumTreePythonMethodSplitter
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
        open val filterPredicates: Collection<MethodFilterPredicate> = emptyList(),
        open val javaParser: String = "gumtree",
        open val pythonParser: String = "antlr"
) : LabelExtractor {

    override fun toLabeledData(
            parseResult: ParseResult<out Node>
    ): List<LabeledResult<out Node>> {
        val (root, filePath) = parseResult
        val fileExtension = File(filePath).extension
        val methodInfos = when (fileExtension) {
            "c", "cpp" -> {
                val methodSplitter = FuzzyMethodSplitter()
                methodSplitter.splitIntoMethods(root as FuzzyNode)
            }
            "java" -> {
                when (javaParser) {
                    "gumtree" -> {
                        val methodSplitter = GumTreeJavaMethodSplitter()
                        methodSplitter.splitIntoMethods(root as GumTreeNode)
                    }
                    "antlr" -> {
                        val methodSplitter = JavaMethodSplitter()
                        methodSplitter.splitIntoMethods(root as AntlrNode)
                    }
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser $javaParser")
                    }
                }
            }
            "py" -> {
                when (pythonParser) {
                    "gumtree" -> {
                        val methodSplitter = GumTreePythonMethodSplitter()
                        methodSplitter.splitIntoMethods(root as GumTreeNode)
                    }
                    "antlr" -> {
                        val methodSplitter = PythonMethodSplitter()
                        methodSplitter.splitIntoMethods(root as AntlrNode)
                    }
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser $pythonParser")
                    }
                }
            }
            "js" -> {
                val methodSplitter = JavaScriptMethodSplitter()
                methodSplitter.splitIntoMethods(root as AntlrNode)
            }
            else -> throw UnsupportedOperationException("Unsupported extension $fileExtension")
        }.filter { methodInfo ->
            filterPredicates.all { predicate ->
                predicate.isFiltered(methodInfo)
            }
        }
        return methodInfos.mapNotNull {
            val label = extractLabel(it, filePath) ?: return@mapNotNull null
            LabeledResult(it.method.root, label, filePath)
        }
    }

    abstract fun <T : Node> extractLabel(methodInfo: MethodInfo<T>, filePath: String): String?
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
        val hideMethodNames: Boolean = false,
        override val filterPredicates: Collection<MethodFilterPredicate> = emptyList(),
        override val javaParser: String = "gumtree",
        override val pythonParser: String = "antlr"
) : MethodLabelExtractor(filterPredicates, javaParser, pythonParser) {

    override fun <T : Node> extractLabel(methodInfo: MethodInfo<T>, filePath: String): String? {
        val methodNameNode = methodInfo.method.nameNode ?: return null
        val methodRoot = methodInfo.method.root
        val methodName = methodInfo.name() ?: return null

        if (hideMethodNames) {
            methodRoot.preOrder().forEach { node ->
                if (node.getToken() == methodName) {
                    node.setTechnicalToken("SELF")
                }
            }
            methodNameNode.setTechnicalToken("METHOD_NAME")
        }
        return methodName
    }
}
