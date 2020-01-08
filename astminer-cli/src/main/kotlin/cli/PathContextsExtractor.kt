package cli

import astminer.common.getNormalizedToken
import astminer.common.model.LabeledPathContexts
import astminer.paths.*
import cli.arguments.PathBasedRepresentationArgs
import cli.util.getGranularityParser
import cli.util.getPbrStorage
import java.io.File


class PathContextsExtractor : PathBasedRepresentationArgs() {
    private fun extract() {
        val outputDir = File(outputRoot)
        for (extension in extensions) {
            val miner = PathMiner(PathRetrievalSettings(maxPathHeight, maxPathWidth))

            val outputDirForLanguage = outputDir.resolve(extension)
            outputDirForLanguage.mkdir()
            val storage = getPbrStorage(pbrStorageType, outputDirForLanguage.path, batchMode, batchSize)

            val parser = getGranularityParser(granularityLevel, splitTokens, hideMethodName)
            val parsed = parser.parseAtGranularityLevel(File(projectRoot), extension)

            parsed.forEach { parseResult ->
                val (filePath, root, originalLabel) = parseResult
                if (root == null) {
                    return@forEach
                }
                val paths = miner.retrievePaths(root).take(maxPathContexts)
                val label = if (storeFilename) {
                    File(filePath).resolve(originalLabel).path
                } else {
                    originalLabel
                }

                storage.store(LabeledPathContexts(label, paths.map {
                    toPathContext(it) { node ->
                        node.getNormalizedToken()
                    }
                }))
            }

            // Save stored data on disk
            storage.save(maxPaths, maxTokens)
        }
    }

    override fun run() {
        extract()
    }
}