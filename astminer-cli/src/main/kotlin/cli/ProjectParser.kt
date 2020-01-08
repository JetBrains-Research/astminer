package cli

import cli.arguments.AstArgs
import cli.util.*
import java.io.File


class ProjectParser : AstArgs() {

    private fun parsing() {
        val outputDir = File(outputRoot)
        for (extension in extensions) {
            // Choose type of storage
            val storage = getAstStorage(astStorageType)

            // Choose granularity level
            val parser = getGranularityParser(granularityLevel, splitTokens, hideMethodName)
            val parsed = parser.parseAtGranularityLevel(File(projectRoot), extension)
            parsed.forEach { parseResult ->
                val (filePath, root, label) = parseResult
                root?.apply {
                    storage.store(root, label = File(filePath).resolve(label).path)
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