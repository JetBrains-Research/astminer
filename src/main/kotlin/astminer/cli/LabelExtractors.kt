package astminer.cli

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.javascript.JavaScriptMethodSplitter
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.cpp.FuzzyNode
import astminer.parse.java.GumTreeJavaNode
import astminer.parse.java.GumTreeMethodSplitter
import java.io.File


data class LabeledParseResult<T : Node>(val root: T, val label: String)


interface LabelExtractor {
    fun toLabeledData(parseResult: ParseResult<out Node>): List<LabeledParseResult<out Node>>
}

abstract class FileLabelExtractor : LabelExtractor {

    override fun toLabeledData(
            parseResult: ParseResult<out Node>
    ): List<LabeledParseResult<out Node>> {
        val (root, filePath) = parseResult
        return if (root == null) {
            emptyList()
        } else {
            val label = extractLabel(root, filePath) ?: return emptyList()
            listOf(LabeledParseResult(root, label))
        }
    }

    abstract fun extractLabel(root: Node, filePath: String): String?
}

abstract class MethodLabelExtractor(
        open val filterPredicates: Collection<MethodFilterPredicate> = emptyList(),
        open val javaParser: String = "gumtree"
) : LabelExtractor {

    override fun toLabeledData(
            parseResult: ParseResult<out Node>
    ): List<LabeledParseResult<out Node>> {
        val (root, filePath) = parseResult
        if (root == null) {
            return emptyList()
        }
        val fileExtension = File(filePath).extension
        val methodInfos = when (fileExtension) {
            "c", "cpp" -> {
                val methodSplitter = FuzzyMethodSplitter()
                methodSplitter.splitIntoMethods(root as FuzzyNode)
            }
            "java" -> {
                when (javaParser) {
                    "gumtree" -> {
                        val methodSplitter = GumTreeMethodSplitter()
                        methodSplitter.splitIntoMethods(root as GumTreeJavaNode)
                    }
                    "antlr" -> {
                        val methodSplitter = JavaMethodSplitter()
                        methodSplitter.splitIntoMethods(root as SimpleNode)
                    }
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser $javaParser")
                    }
                }
            }
            "py" -> {
                val methodSplitter = PythonMethodSplitter()
                methodSplitter.splitIntoMethods(root as SimpleNode)
            }
            "js" -> {
                val methodSplitter = JavaScriptMethodSplitter()
                methodSplitter.splitIntoMethods(root as SimpleNode)
            }
            else -> throw UnsupportedOperationException("Unsupported extension $fileExtension")
        }.filter { methodInfo ->
            filterPredicates.all { predicate ->
                predicate.isFiltered(methodInfo)
            }
        }
        return methodInfos.mapNotNull {
            val label = extractLabel(it, filePath) ?: return@mapNotNull null
            LabeledParseResult(it.method.root, label)
        }
    }

    abstract fun <T : Node> extractLabel(methodInfo: MethodInfo<T>, filePath: String): String?
}

class FilePathExtractor : FileLabelExtractor() {
    override fun extractLabel(root: Node, filePath: String): String? {
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
        override val javaParser: String = "gumtree"
) : MethodLabelExtractor(filterPredicates, javaParser) {

    override fun <T : Node> extractLabel(methodInfo: MethodInfo<T>, filePath: String): String? {
        val methodNameNode = methodInfo.method.nameNode ?: return null
        val methodRoot = methodInfo.method.root
        val methodName = methodInfo.name() ?: return null

        if (hideMethodNames) {
            methodRoot.preOrder().forEach { node ->
                if (node.getToken() == methodName) {
                    node.setNormalizedToken("SELF")
                }
            }
            methodNameNode.setNormalizedToken("METHOD_NAME")
        }
        return methodName
    }
}
