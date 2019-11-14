package cli

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.common.splitToSubtokens
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.cpp.FuzzyNode


interface Granularity {

    val isTokenSplitted: Boolean

    fun splitByGranularityLevel(parseResults: List<ParseResult<out Node>>, fileExtension: String): List<ParseResult<out Node>>

}


class FileGranularity(override val isTokenSplitted: Boolean) : Granularity {

    override fun splitByGranularityLevel(parseResults: List<ParseResult<out Node>>, fileExtension: String): List<ParseResult<out Node>> {
        parseResults.forEach {
            it.root?.preOrder()?.forEach { node ->
                if (isTokenSplitted) {
                    node.setNormalizedToken(splitToSubtokens(node.getToken()).joinToString("|"))
                } else {
                    node.setNormalizedToken()
                }
            }
        }
        return parseResults
    }

}


class MethodGranularity(override val isTokenSplitted: Boolean,
                        private val isMethodNameHide: Boolean = false) : Granularity {


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
            methodRoot.preOrder().forEach { node ->
                if (isTokenSplitted) {
                    node.setNormalizedToken(splitToSubtokens(node.getToken()).joinToString("|"))
                } else {
                    node.setNormalizedToken()
                }
            }
            if (isMethodNameHide) {
                methodNameNode.setNormalizedToken("METHOD_NAME")
            }
            processedMethods.add(ParseResult(methodRoot, label))
        }
        return processedMethods
    }

}