package astminer.cli

import astminer.ast.CsvAstStorage
import astminer.ast.DotAstStorage
import astminer.common.getProjectFilesWithExtension
import astminer.common.model.AstStorage
import astminer.common.preOrder
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import java.io.File

class ProjectParser(private val customLabelExtractor: LabelExtractor? = null) : CliktCommand() {

    private val supportedLanguages = listOf("java", "c", "cpp", "py", "js")

    val extensions: List<String> by option(
        "--lang",
        help = "Comma-separated list of file extensions that will be parsed.\n" +
                "Supports 'c', 'cpp', 'java', 'py', 'js', defaults to all these extensions."
    ).split(",").default(supportedLanguages)

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
        help = "AST storage type ('dot' or 'csv', defaults to 'csv')"
    ).default("csv")

    val granularityLevel: String by option(
        "--granularity",
        help = "Choose level of granularity ('file' or 'method', defaults to 'file')"
    ).default("file")

    val isMethodNameHide: Boolean by option(
        "--hide-method-name",
        help = "if passed with method level granularity, the names of all methods are replaced with placeholder token"
    ).flag(default = false)

    val isTokenSplitted: Boolean by option(
        "--split-tokens",
        help = "if passed, split tokens into sequence of tokens"
    ).flag(default = false)

    val excludeModifiers: List<String> by option(
        "--filter-modifiers",
        help = "Comma-separated list of function's modifiers, which should be filtered." +
                "Works only for method-level granulation."
    ).split(",").default(emptyList())

    val excludeAnnotations: List<String> by option(
        "--filter-annotations",
        help = "Comma-separated list of function's annotations, which should be filtered." +
                "Works only for method-level granulation."
    ).split(",").default(emptyList())

    val filterConstructors: Boolean by option(
        "--remove-constructors",
        help = "Remove constructor methods, works for method-level granulation"
    ).flag(default = false)

    val excludeNodes: List<String> by option(
        "--remove-nodes",
        help = "Comma-separated list of node types, which must be removed from asts."
    ).split(",").default(emptyList())

    val javaParser: String by option(
        "--java-parser",
        help = "Choose a parser for .java files." +
                "'gumtree' for GumTree parser, 'antlr' for antlr parser."
    ).default("gumtree")

    val maxMethodNameLength: Int by option(
        "--max-method-name-length",
        help = "Filtering methods with a large sequence of subtokens in their names"
    ).int().default(-1)

    val maxTokenLength: Int by option(
        "--max-token-length",
        help = "Filter methods containing a long sequence of subtokens in the ast node"
    ).int().default(-1)

    val maxTreeSize: Int by option(
        "--max-tree-size",
        help = "Filter methods by their ast size"
    ).int().default(-1)

    val folderLabel: Boolean by option(
            "--folder-label",
            help = "if passed with file-level granularity, the folder name is used to label paths"
    ).flag(default = false)


    private fun getStorage(storageType: String, directoryPath: String): AstStorage {
        return when (storageType) {
            "csv" -> CsvAstStorage(directoryPath)
            "dot" -> DotAstStorage(directoryPath)
            else -> {
                throw UnsupportedOperationException("Unsupported AST storage $storageType")
            }
        }
    }

    private fun parsing(labelExtractor: LabelExtractor) {
        val outputDir = File(outputDirName)
        for (extension in extensions) {
            // Create directory for current extension
            val outputDirForLanguage = outputDir.resolve(extension)
            // Choose type of storage
            val storage = getStorage(astStorageType, outputDirForLanguage.path)
            // Choose type of parser
            val parser = getParser(
                    extension,
                    javaParser
            )
            // Parse project
            val filesToParse = getProjectFilesWithExtension(File(projectRoot), extension)
            parser.parseFiles(filesToParse) { parseResult ->
                normalizeParseResult(parseResult, isTokenSplitted)
                val labeledParseResults = labelExtractor.toLabeledData(parseResult)
                labeledParseResults.forEach { (root, label) ->
                    root.preOrder().forEach { node ->
                        excludeNodes.forEach { node.removeChildrenOfType(it) }
                    }
                    root.apply {
                        // Save AST as it is or process it to extract features / path-based representations
                        storage.store(root, label, parseResult.filePath)
                    }
                }
            }
            // Save stored data on disk
            storage.close()
        }

    }

    override fun run() {
        val labelExtractor = customLabelExtractor ?: getLabelExtractor(
                granularityLevel,
                javaParser,
                isMethodNameHide,
                excludeModifiers,
                excludeAnnotations,
                filterConstructors,
                maxMethodNameLength,
                maxTokenLength,
                maxTreeSize,
                folderLabel
        )
        parsing(labelExtractor)
    }
}