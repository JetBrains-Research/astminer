package cli

import astminer.common.*
import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.cpp.FuzzyNode
import astminer.parse.java.GumTreeJavaNode
import astminer.parse.java.GumTreeMethodSplitter
import java.io.File


interface Granularity {
    val splitTokens: Boolean
    fun splitByGranularityLevel(parseResults: List<ParseResult<out Node>>, fileExtension: String): List<ParseResult<out Node>>
}


class FileGranularity(override val splitTokens: Boolean) : Granularity {
    override fun splitByGranularityLevel(parseResults: List<ParseResult<out Node>>, fileExtension: String): List<ParseResult<out Node>> {
        parseResults.forEach {
            it.root?.preOrder()?.forEach { node -> processNodeToken(node, splitTokens) }
        }
        return parseResults
    }
}


class MethodGranularity(override val splitTokens: Boolean,
                        private val hideMethodNames: Boolean = false,
                        private val filterConstructors: Boolean = false) : Granularity {
    override fun splitByGranularityLevel(parseResults: List<ParseResult<out Node>>, fileExtension: String): List<ParseResult<out Node>> {
        val filteredParseResults = parseResults.filter { it.root != null }
        return when (fileExtension) {
            "c", "cpp" -> {
                val methodSplitter = FuzzyMethodSplitter()
                filteredParseResults.map { Pair(it.root as FuzzyNode, it.filePath) }
                                    .map { Pair(methodSplitter.splitIntoMethods(it.first), it.second) }
                                    .flatMap { processMethods(it.first, it.second) }
            }
            "java" -> {
                val methodSplitter = GumTreeMethodSplitter()
                filteredParseResults.map { Pair(it.root as GumTreeJavaNode, it.filePath) }
                                    .map { Pair(methodSplitter.splitIntoMethods(it.first), it.second) }
                                    .flatMap { processMethods(it.first, it.second) }
            }
            "py" -> {
                val methodSplitter = PythonMethodSplitter()
                filteredParseResults.map { Pair(it.root as SimpleNode, it.filePath) }
                                    .map { Pair(methodSplitter.splitIntoMethods(it.first), it.second) }
                                    .flatMap { processMethods(it.first, it.second) }
            }
            else -> throw UnsupportedOperationException("Unsupported extension $fileExtension")
        }
    }

    private fun processMethods(methods: Collection<MethodInfo<out Node>>, filePath: String): List<ParseResult<out Node>> {
        val processedMethods = mutableListOf<ParseResult<Node>>()
        methods.forEach {
            val methodNameNode = it.method.nameNode ?: return@forEach
            val methodRoot = it.method.root
            var methodName = methodNameNode.getToken()

            if (filterConstructors && methodName == it.enclosingElementName()) {
                return@forEach
            }

            methodRoot.preOrder().forEach { node -> processNodeToken(node, splitTokens) }
            if (hideMethodNames) {
                methodNameNode.setNormalizedToken("METHOD_NAME")
            }
            if (splitTokens) {
                methodName = separateToken(methodName)
            }
            val methodFilePath = File(filePath).resolve(methodName).path
            processedMethods.add(ParseResult(methodRoot, methodFilePath))
        }
        return processedMethods
    }
}

fun separateToken(token: String, separator: CharSequence = "|"): String {
    return splitToSubtokens(token).joinToString(separator)
}

fun processNodeToken(node: Node, splitToken: Boolean) {
    if (splitToken) {
        node.setNormalizedToken(separateToken(node.getToken()))
    } else {
        node.setNormalizedToken()
    }
}

