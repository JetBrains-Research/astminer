package cli.util

import astminer.common.model.MethodInfo
import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.common.model.TreeMethodSplitter
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.parse.java.GumTreeJavaParser
import astminer.parse.java.GumTreeMethodSplitter
import java.io.File

/**
 * @param parser class that implements parsing
 * @param extension file extension to choose files for parsing
 */
data class SupportedLanguage<T : Node>(
    val parser: Parser<T>,
    val methodSplitter: TreeMethodSplitter<T>,
    val extension: String,
    val backend: String? = null
) {
    fun parse(root: File) = parser.parseWithExtension(root, extension)

    fun parseIntoMethods(root: File) = parser.parseWithExtension(root, extension).mapNotNull { parseResult ->
        parseResult.root?.let {
            FileMethods(methodSplitter.splitIntoMethods(it), parseResult.filePath)
        }
    }
}

/**
 * List of supported language extensions and corresponding parsers.
 */
val supportedLanguages = listOf(
    SupportedLanguage(GumTreeJavaParser(), GumTreeMethodSplitter(),"java", "gumtree"),
    SupportedLanguage(JavaParser(), JavaMethodSplitter(), "java", "antlr"),
    SupportedLanguage(FuzzyCppParser(), FuzzyMethodSplitter(), "c", "fuzzy"),
    SupportedLanguage(FuzzyCppParser(), FuzzyMethodSplitter(), "cpp", "fuzzy"),
    SupportedLanguage(PythonParser(), PythonMethodSplitter(), "py", "antlr")
)

fun getParser(extension: String): SupportedLanguage<out Node> {
    for (language in supportedLanguages) {
        if (extension == language.extension) {
            return language
        }
    }
    throw UnsupportedOperationException("Unsupported extension $extension")
}

data class FileMethods(val methods: Collection<MethodInfo<out Node>>, val sourceFile: String)
