package astminer.cli

import astminer.common.getNormalizedToken
import astminer.common.model.*
import astminer.common.preOrder
import astminer.common.setNormalizedToken
import astminer.paths.Code2VecPathStorage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import com.github.ajalt.clikt.core.CliktCommand
import java.io.File

class PathContextsExtractor : CliktCommand() {

    private fun getParser(extension: String): Parser<out Node> {
        for (language in CliRunner().supportedLanguages) {
            if (extension == language.extension) {
                return language.parser
            }
        }
        throw UnsupportedOperationException("Unsupported extension $extension")
    }

    fun extractPathContexts() {
        val outputDir = File(CliRunner().outputDir)
        for (extension in CliRunner().extensions) {
            val miner = PathMiner(PathRetrievalSettings(CliRunner().maxPathHeight, CliRunner().maxPathWidth))
            val parser = getParser(extension)
            val parsedFiles = parser.parseWithExtension(File(CliRunner().projectRoot), extension)

            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            val storage = Code2VecPathStorage(outputDirForLanguage.path, CliRunner().batchMode, CliRunner().batchSize)

            parsedFiles.forEach { parseResult ->
                val root = parseResult.root ?: return@forEach
                val filePath = parseResult.filePath

                root.preOrder().forEach { node -> node.setNormalizedToken() }

                val paths = miner.retrievePaths(root).take(CliRunner().maxPathContexts)
                storage.store(LabeledPathContexts(filePath, paths.map { astPath ->
                    toPathContext(astPath) { node ->
                        node.getNormalizedToken()
                    }
                }))
            }

            // Save stored data on disk
            storage.save(CliRunner().maxPaths, CliRunner().maxTokens)
        }
    }

    override fun run() {
        extractPathContexts()
    }
}