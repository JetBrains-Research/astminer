package cli

import astminer.common.*
import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.cpp.FuzzyNode


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
                        private val hideMethodNames: Boolean = false) : Granularity {
    override fun splitByGranularityLevel(parseResults: List<ParseResult<out Node>>, fileExtension: String): List<ParseResult<out Node>> {
        val filteredParseResults = parseResults.filter { it.root != null }
        return processMethods(when (fileExtension) {
            "c", "cpp" -> {
                val methodSplitter = FuzzyMethodSplitter()
                filteredParseResults.map { it.root as FuzzyNode }.flatMap { methodSplitter.splitIntoMethods(it) }
            }
            "java" -> {
                val methodSplitter = JavaMethodSplitter()
                filteredParseResults.map { it.root as SimpleNode }.flatMap { methodSplitter.splitIntoMethods(it) }
            }
            "py" -> {
                val methodSplitter = PythonMethodSplitter()
                filteredParseResults.map { it.root as SimpleNode }.flatMap { methodSplitter.splitIntoMethods(it) }
            }
            else -> throw UnsupportedOperationException("Unsupported extension $fileExtension")
        })
    }

    private fun processMethods(methods: List<MethodInfo<out Node>>): List<ParseResult<out Node>> {
        val processedMethods = mutableListOf<ParseResult<Node>>()
        methods.forEach {
            val methodNameNode = it.method.nameNode ?: return@forEach
            val methodRoot = it.method.root
            val label = methodNameNode.getToken()
            methodRoot.preOrder().forEach { node -> processNodeToken(node, splitTokens) }
            if (hideMethodNames) {
                methodNameNode.setNormalizedToken("METHOD_NAME")
            }
            processedMethods.add(ParseResult(methodRoot, label))
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

