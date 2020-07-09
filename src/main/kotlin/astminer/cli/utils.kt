package astminer.cli

import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.java.GumTreeJavaParser
import astminer.common.model.Node
import astminer.common.model.Parser

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
        else -> {
            throw UnsupportedOperationException("Unsupported extension $extension")
        }
    }
}

fun getGranularity(
        granularityLevel: String,
        javaParser: String,
        isTokenSplitted: Boolean,
        isMethodNameHide: Boolean,
        excludeModifiers: List<String>,
        excludeAnnotations: List<String>,
        filterConstructors: Boolean,
        maxMethodNameLength: Int,
        maxTokenLength: Int,
        maxTreeSize: Int
): Granularity {
    when (granularityLevel) {
        "file" -> return FileGranularity(isTokenSplitted)
        "method" -> {
            val filterPredicates = mutableListOf(
                    ModifierFilterPredicate(excludeModifiers), AnnotationFilterPredicate(excludeAnnotations),
                    MethodNameLengthFilterPredicate(maxMethodNameLength), TokenLengthFilterPredicate(maxTokenLength),
                    TreeSizeFilterPredicate(maxTreeSize)
            )
            if (filterConstructors) {
                filterPredicates.add(ConstructorFilterPredicate())
            }
            return MethodGranularity(isTokenSplitted, isMethodNameHide, filterPredicates, javaParser)
        }
    }
    throw UnsupportedOperationException("Unsupported granularity level $granularityLevel")
}
