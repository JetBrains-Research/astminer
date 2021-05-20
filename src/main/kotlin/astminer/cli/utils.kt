package astminer.cli

import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.common.model.Node
import astminer.common.model.Parser
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


fun getLabelExtractor(
    granularityLevel: String,
    javaParser: String,
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
                MethodNameWordsNumberFilter(maxMethodNameLength), MethodAnyNodeWordsNumberFilter(maxTokenLength),
                TreeSizeFilterPredicate(maxTreeSize)
            )
            if (filterConstructors) {
                filterPredicates.add(ConstructorFilterPredicate)
            }
            return MethodNameExtractor(filterPredicates, javaParser)
        }
    }
    throw UnsupportedOperationException("Unsupported granularity level $granularityLevel")
}
