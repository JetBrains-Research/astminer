package cli

import astminer.ast.CsvAstStorage
import astminer.ast.DotAstStorage
import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.parse.cpp.FuzzyCppParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.split
import java.io.File


class ProjectParser : CliktCommand() {

    /**
     * @param parser class that implements parsing
     * @param extension file extension to choose files for parsing
     */
    private data class SupportedLanguage(val parser: Parser<out Node>, val extension: String)

    /**
     * @param astStorage class that implements ast's storage
     * @param type name of storage
     */
    private data class SupportedAstStorage(val astStorage: AstStorage, val type: String)

    /**
     * List of supported language extensions and corresponding parsers.
     */
    private val supportedLanguages = listOf(
        SupportedLanguage(JavaParser(), "java"),
        SupportedLanguage(FuzzyCppParser(), "c"),
        SupportedLanguage(FuzzyCppParser(), "cpp"),
        SupportedLanguage(PythonParser(), "py")
    )

    private val supportedAstStorages = listOf(
        SupportedAstStorage(CsvAstStorage(), "csv"),
        SupportedAstStorage(DotAstStorage(), "dot")
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

    val astStorageType: String by option(
        "--storage",
        help = "AST storage type (dot-files or csv)"
    ).default(supportedAstStorages[0].type)

    private fun getParser(extension: String): Parser<out Node> {
        for (language in supportedLanguages) {
            if (extension == language.extension) {
                return language.parser
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
    }

    private fun getStorage(storageType: String): AstStorage {
        for (storage in supportedAstStorages) {
            if (storageType == storage.type) {
                return storage.astStorage
            }
        }
        throw java.lang.UnsupportedOperationException("Unsupported AST storage $storageType")
    }


    private fun parsing() {
        val outputDir = File(outputDirName)
        for (extension in extensions) {
            // Choose type of storage
            val storage = getStorage(astStorageType)
            val parser = getParser(extension)
            val roots = parser.parseWithExtension(File(projectRoot), extension)
            roots.forEach { parseResult ->
                val root = parseResult.root
                val filePath = parseResult.filePath
                root?.apply {
                    // Save AST as it is or process it to extract features / path-based representations
                    storage.store(root, label = filePath)
                }
            }
            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            // Save stored data on disk
            storage.save(outputDirForLanguage.path)
        }

    }

    override fun run() {
        parsing()
    }
}