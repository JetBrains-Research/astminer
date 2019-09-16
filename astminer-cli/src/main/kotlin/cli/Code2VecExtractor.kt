package cli

import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.common.model.TreeMethodSplitter
import astminer.parse.antlr.java.JavaMethodSplitter
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonMethodSplitter
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import astminer.parse.cpp.FuzzyMethodSplitter
import astminer.paths.Code2VecPathStorage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import com.github.ajalt.clikt.parameters.types.int
import java.io.File

class Code2VecExtractor : CliktCommand() {

    /**
     * @param parser class that implements parsing
     * @param extension file extension to choose files for parsing
     */
    private data class SupportedLanguage<T : Node>(
        val parser: Parser<T>,
        val methodSplitter: TreeMethodSplitter<T>,
        val extension: String
    )

    /**
     * List of supported language extensions and corresponding parsers.
     */
    private val supportedLanguages = listOf(
        SupportedLanguage(JavaParser(), JavaMethodSplitter(),"java"),
        SupportedLanguage(FuzzyCppParser(), FuzzyMethodSplitter(),"c"),
        SupportedLanguage(FuzzyCppParser(), FuzzyMethodSplitter(), "cpp"),
        SupportedLanguage(PythonParser(), PythonMethodSplitter(), "py")
    )

    val extensions: List<String> by option(
        "--lang",
        help = "File extensions that will be parsed"
    ).split(",").default(supportedLanguages.map { it.extension })

    val projectRoot: String by option(
        "--project",
        help = "Path to the project that will be parsed"
    ).required()

    val outputDirName: String by option(
        "--output",
        help = "Path to directory where the output will be stored"
    ).required()

    val maxPathHeight: Int by option(
        "--maxH",
        help = "Maximum height of path for code2vec"
    ).int().default(8)

    val maxPathWidth: Int by option(
        "--maxW",
        help = "Maximum width of path. " +
                "Note, that here width is the difference between token indices in contrast to the original code2vec."
    ).int().default(3)

    private fun getParser(extension: String): Parser<out Node> {
        for (language in supportedLanguages) {
            if (extension == language.extension) {
                return language.parser
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
    }

    private fun getMethodSplitter(extension: String): TreeMethodSplitter<out Node> {
        for (language in supportedLanguages) {
            if (extension == language.extension) {
                return language.methodSplitter
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
    }

    fun extract() {
        val outputDir = File(outputDirName)
        for (extension in extensions) {
            val miner = PathMiner(PathRetrievalSettings(maxPathHeight, maxPathWidth))
            val storage = Code2VecPathStorage()
            val parser = getParser(extension)
            val methodSplitter = getMethodSplitter(extension)

            val roots = parser.parseWithExtension(File(projectRoot), extension)
            roots.forEach { parseResult ->
                val root = parseResult.root
                val filePath = parseResult.filePath
                val methods = methodSplitter.splitIntoMethods(root)

                root?.apply {
                    // Save AST as it is or process it to extract features / path-based representations
//                    storage.store(root, label = filePath)
                }
            }
            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            // Save stored data on disk
            storage.save(outputDirForLanguage.path)
        }
    }

    override fun run() {
        extract()
    }
}