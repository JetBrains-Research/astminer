package examples

import astminer.common.Node
import astminer.common.Parser
import astminer.featureextraction.className
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import java.io.File
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

    val extensions: List<String> by option("--lang", help = "File extensions that should be parsed").split(",").required()
    val projectRoot: String by option("--project", help = "Path to the project that should be parsed").required()
    val output: String by option("--output", help = "Path to directory where the output should be stored").required()

    private fun getParser(extension: String): Parser<out Node> {
        for (language in supportedLanguages) {
            if (extension == language.extension) {
                return language.parser
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
    }

    override fun run() {
        for (extension in extensions) {
            val parser = getParser(extension)
            println(parser.className())
//            parser.parseProject(File(projectRoot))
        }
    }
}

fun main(args: Array<String>) = ProjectParser().main(args)
