package examples

import astminer.ast.VocabularyAstStorage
import astminer.common.Node
import astminer.common.Parser
import astminer.featureextraction.className
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import java.io.File
import java.lang.Exception
import java.lang.UnsupportedOperationException

class ProjectParser : CliktCommand() {

    /**
     * @param parser class that implements parsing
     * @param extension file extension to choose files for parsing
     */
    private data class SupportedLanguage(val parser: Parser<out Node>, val extension: String)

    /**
     * List of supported languages
     */
    private val supportedLanguages = listOf(
        SupportedLanguage(JavaParser(), "java"),
        SupportedLanguage(FuzzyCppParser(), "c"),
        SupportedLanguage(FuzzyCppParser(), "cpp"),
        SupportedLanguage(PythonParser(), "py")
    )

    val extensions: List<String> by option("--lang", help = "File extensions that will be parsed")
        .split(",")
        .default(supportedLanguages.map { it.extension })

    val projectRoot: String by option("--project", help = "Path to the project that will be parsed").required()
    val shouldPreprocess: Boolean by option(
        "--preprocess", help =
        "If the flag is set, the project will be preprocessed before parsing"
    ).flag(default = false)

    val preprocessDir: String? by option("--preprocDir", "Path to directory where the preprocessed data will be stored")
    val outputDir: String? by option("--output", help = "Path to directory where the output will be stored")

    private fun getParser(extension: String): Parser<out Node> {
        for (language in supportedLanguages) {
            if (extension == language.extension) {
                return language.parser
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
    }

    private fun preprocessing() {
        if (preprocessDir != null) {
            throw Exception("If the preprocessing flag was set, you should provide proprocessDir path")
        } else {
            val parser = FuzzyCppParser()
            parser.preprocessProject(File(projectRoot), File(preprocessDir))
        }
    }

    private fun parsing() {
        val storage = VocabularyAstStorage()
        for (extension in extensions) {
            val parser = getParser(extension)
            val roots = parser.parseWithExtension(File(projectRoot), extension)
            roots.filterNotNull().forEach { root ->
//                storage.store(root, entityId = )
            }
        }
    }

    override fun run() {
        if (shouldPreprocess) {
            preprocessing()
        } else {
            parsing()
        }
    }
}

fun main(args: Array<String>) = ProjectParser().main(args)
