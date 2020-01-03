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
                        private val hideMethodNames: Boolean = false) : Granularity {

    private data class FileMethods(val methods: Collection<MethodInfo<out Node>>, val sourceFile: String)

    override fun splitByGranularityLevel(parseResults: List<ParseResult<out Node>>, fileExtension: String): List<ParseResult<out Node>> {
        val filteredParseResults = parseResults.filter { it.root != null }
        return when (fileExtension) {
            "c", "cpp" -> {
                val methodSplitter = FuzzyMethodSplitter()
                filteredParseResults.map {
                    FileMethods(methodSplitter.splitIntoMethods(it.root as FuzzyNode), it.filePath)
                }
            }
            "java" -> {
                val methodSplitter = GumTreeMethodSplitter()
                filteredParseResults.map {
                    FileMethods(methodSplitter.splitIntoMethods(it.root as GumTreeJavaNode), it.filePath)
                }
            }
            "py" -> {
                val methodSplitter = PythonMethodSplitter()
                filteredParseResults.map {
                    FileMethods(methodSplitter.splitIntoMethods(it.root as SimpleNode), it.filePath)
                }
            }
            else -> throw UnsupportedOperationException("Unsupported extension $fileExtension")
        }.flatMap{ processMethods(it) }
    }

    private fun processMethods(fileMethods: FileMethods): List<ParseResult<out Node>> {
        val processedMethods = mutableListOf<ParseResult<Node>>()
        fileMethods.methods.forEach {
            val methodNameNode = it.method.nameNode ?: return@forEach
            val methodRoot = it.method.root
            var label = methodNameNode.getToken()
            methodRoot.preOrder().forEach { node -> processNodeToken(node, splitTokens) }
            if (hideMethodNames) {
                methodNameNode.setNormalizedToken("METHOD_NAME")
            }
            if (splitTokens) {
                label = separateToken(label)
            }
            val methodFilePath = File(fileMethods.sourceFile).resolve(label).path
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

