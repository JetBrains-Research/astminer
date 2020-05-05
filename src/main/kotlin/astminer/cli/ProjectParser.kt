package astminer.cli

import astminer.common.model.AstStorage
import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.common.preOrder
import astminer.parse.antlr.java.JavaParser
import astminer.parse.java.GumTreeJavaParser
import java.io.File

class ProjectParser {

    private fun getParser(extension: String): Parser<out Node> {
        val javaParser = CliRunner().javaParser
        return when (extension) {
            "java" -> {
                when (CliRunner().javaParser) {
                    "gumtree" -> GumTreeJavaParser()
                    "antlr" -> JavaParser()
                    else -> {
                        throw UnsupportedOperationException("Unsupported parser for java extension $javaParser")
                    }
                }
            }
            else -> {
                CliRunner().supportedLanguages.find { it.extension == extension }?.parser
                    ?: throw UnsupportedOperationException("Unsupported extension $extension")
            }
        }
    }

    private fun getStorage(storageType: String): AstStorage {
        for (storage in CliRunner().supportedAstStorages) {
            if (storageType == storage.type) {
                return storage.astStorage
            }
        }
        throw UnsupportedOperationException("Unsupported AST storage $storageType")
    }

    private fun getGranularity(granularityLevel: String): Granularity {
        when (granularityLevel) {
            "file" -> return FileGranularity(CliRunner().isTokenSplitted)
            "method" -> {
                val filterPredicates = mutableListOf(
                    ModifierFilterPredicate(CliRunner().excludeModifiers), AnnotationFilterPredicate(CliRunner().excludeAnnotations),
                    MethodNameLengthFilterPredicate(CliRunner().maxMethodNameLength), TokenLengthFilterPredicate(CliRunner().maxTokenLength),
                    TreeSizeFilterPredicate(CliRunner().maxTreeSize)
                )
                if (CliRunner().filterConstructors) {
                    filterPredicates.add(ConstructorFilterPredicate())
                }
                return MethodGranularity(CliRunner().isTokenSplitted, CliRunner().isMethodNameHide, filterPredicates, CliRunner().javaParser)
            }
        }
        throw UnsupportedOperationException("Unsupported granularity level $granularityLevel")
    }

    fun parsing() {
        val outputDir = File(CliRunner().outputDir)
        for (extension in CliRunner().extensions) {
            // Choose type of storage
            val storage = getStorage(CliRunner().astStorageType)
            // Choose type of parser
            val parser = getParser(extension)
            // Choose granularity level
            val granularity = getGranularity(CliRunner().granularityLevel)
            // Parse project
            val parsedProject = parser.parseWithExtension(File(CliRunner().projectRoot), extension)
            // Split project to required granularity level
            val roots = granularity.splitByGranularityLevel(parsedProject, extension)
            roots.forEach { parseResult ->
                val root = parseResult.root
                val filePath = parseResult.filePath
                root?.preOrder()?.forEach { node ->
                    CliRunner().excludeNodes.forEach { node.removeChildrenOfType(it) }
                }
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
}