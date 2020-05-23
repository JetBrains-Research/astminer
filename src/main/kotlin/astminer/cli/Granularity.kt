package astminer.cli

import astminer.common.*
import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.java.JavaMethodSplitter
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
                        private val filterPredicates: Collection<MethodFilterPredicate> = emptyList(),
                        private val javaParser: String = "gumtree") : Granularity {

    private data class FileMethods(var methods: Collection<MethodInfo<out Node>>, val sourceFile: String)

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
                when (javaParser) {
                    "gumtree" -> {
                        val methodSplitter = GumTreeMethodSplitter()
                        filteredParseResults.map {
                            FileMethods(methodSplitter.splitIntoMethods(it.root as GumTreeJavaNode), it.filePath)
                        }
                    }
                    "antlr" -> {
                        val methodSplitter = JavaMethodSplitter()
                        filteredParseResults.map {
                            FileMethods(methodSplitter.splitIntoMethods(it.root as SimpleNode), it.filePath)
                        }
                    }
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser $javaParser")
                    }
                }
            }
            "py" -> {
                val methodSplitter = PythonMethodSplitter()
                filteredParseResults.map {
                    FileMethods(methodSplitter.splitIntoMethods(it.root as SimpleNode), it.filePath)
                }
            }
            else -> throw UnsupportedOperationException("Unsupported extension $fileExtension")
        }.flatMap { fileMethods ->
            filterPredicates.forEach { predicate ->
                fileMethods.methods = fileMethods.methods.filter { predicate.isFiltered(it) }
            }
            processMethods(fileMethods)
        }
    }

    private fun processMethods(fileMethods: FileMethods): List<ParseResult<out Node>> {
        val processedMethods = mutableListOf<ParseResult<Node>>()
        fileMethods.methods.forEach {
            val methodNameNode = it.method.nameNode ?: return@forEach
            val methodRoot = it.method.root
            var methodName = methodNameNode.getToken()

            methodRoot.preOrder().forEach { node -> processNodeToken(node, splitTokens) }
            if (hideMethodNames) {
                methodRoot.preOrder().forEach { node ->
                    if (node.getToken() == methodName) {
                        node.setNormalizedToken("SELF")
                    }
                }
                methodNameNode.setNormalizedToken("METHOD_NAME")
            }
            if (splitTokens) {
                methodName = separateToken(methodName)
            }
            val methodFilePath = File(fileMethods.sourceFile).resolve(methodName).path
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
