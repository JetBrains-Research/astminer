package cli

import astminer.common.*
import astminer.common.model.Node
import astminer.common.model.ParseResult
import cli.util.FileMethods
import cli.util.GranularityParseResult
import cli.util.getParser
import java.io.File


interface GranularityParser {
    val splitTokens: Boolean
    fun parseAtGranularityLevel(projectRoot: File, fileExtension: String): List<GranularityParseResult<out Node>>
}


class FileParser(override val splitTokens: Boolean) : GranularityParser {
    override fun parseAtGranularityLevel(
        projectRoot: File,
        fileExtension: String
    ): List<GranularityParseResult<out Node>> {
        val parseResults = getParser(fileExtension).parse(projectRoot)
        parseResults.forEach {
            it.root?.preOrder()?.forEach { node -> processNodeToken(node, splitTokens) }
        }
        return parseResults.map { GranularityParseResult(it.filePath, it.root) }
    }
}


class MethodParser(
    override val splitTokens: Boolean,
    private val hideMethodNames: Boolean = false
) : GranularityParser {

    override fun parseAtGranularityLevel(
        projectRoot: File,
        fileExtension: String
    ): List<GranularityParseResult<out Node>> {
        val methods = getParser(fileExtension).parseIntoMethods(projectRoot)
        return methods.flatMap { processMethods(it) }
    }

    private fun processMethods(fileMethods: FileMethods): List<GranularityParseResult<out Node>> {
        val processedMethods = mutableListOf<GranularityParseResult<Node>>()
        fileMethods.methods.forEach {
            val methodNameNode = it.method.nameNode ?: return@forEach
            val methodRoot = it.method.root
            val label = if (splitTokens) {
                separateToken(methodNameNode.getToken())
            } else {
                methodNameNode.getToken()
            }

            methodRoot.preOrder().forEach { node -> processNodeToken(node, splitTokens) }
            if (hideMethodNames) {
                methodNameNode.setNormalizedToken("METHOD_NAME")
            }

            processedMethods.add(GranularityParseResult(fileMethods.sourceFile, methodRoot, label))
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
