package astminer.cli

import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.java.GumTreeJavaParser
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.common.model.Parser
import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.common.splitToSubtokens
import astminer.parse.antlr.javascript.JavaScriptParser

fun getParser(
        extension: String,
        javaParser: String
): Parser<out Node> {
    return when (extension) {
        "java" -> {
            when (javaParser) {
                "gumtree" -> GumTreeJavaParser()
                "antlr" -> JavaParser()
                else -> {
                    throw UnsupportedOperationException("Unsupported parser for java extension $javaParser")
                }
            }
        }
        "c" -> FuzzyCppParser()
        "cpp" -> FuzzyCppParser()
        "py" -> PythonParser()
        "js" -> JavaScriptParser()
        else -> {
            throw UnsupportedOperationException("Unsupported extension $extension")
        }
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

fun <T : Node> normalizeParseResult(parseResult: ParseResult<T>, splitTokens: Boolean) {
    parseResult.root?.preOrder()?.forEach { node -> processNodeToken(node, splitTokens) }
}

fun getLabelExtractor(
        granularityLevel: String,
        javaParser: String,
        hideMethodNames: Boolean,
        excludeModifiers: List<String>,
        excludeAnnotations: List<String>,
        filterConstructors: Boolean,
        maxMethodNameLength: Int,
        maxTokenLength: Int,
        maxTreeSize: Int,
        useFolderName: Boolean
): LabelExtractor {
    when (granularityLevel) {
        "file" -> {
            return if (useFolderName) {
                FolderExtractor()
            } else {
                FilePathExtractor()
            }
        }
        "method" -> {
            val filterPredicates = mutableListOf(
                    ModifierFilterPredicate(excludeModifiers), AnnotationFilterPredicate(excludeAnnotations),
                    MethodNameLengthFilterPredicate(maxMethodNameLength), TokenLengthFilterPredicate(maxTokenLength),
                    TreeSizeFilterPredicate(maxTreeSize)
            )
            if (filterConstructors) {
                filterPredicates.add(ConstructorFilterPredicate())
            }
            return MethodNameExtractor(hideMethodNames, filterPredicates, javaParser)
        }
    }
    throw UnsupportedOperationException("Unsupported granularity level $granularityLevel")
}
