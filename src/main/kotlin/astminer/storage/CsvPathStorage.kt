package astminer.storage

import astminer.cli.separateToken
import astminer.common.model.Node
import astminer.common.model.PathContextId

class CsvPathStorage(
    outputDirectoryPath: String,
    config: CountingPathStorageConfig,
    tokenProcessor: TokenProcessor = identityTokenProcessor
) :
    CountingPathStorage(outputDirectoryPath, config, tokenProcessor) {

    override fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String {
        val joinedPathContexts = pathContextIds.joinToString(";") { pathContextId ->
            "${pathContextId.startTokenId} ${pathContextId.pathId} ${pathContextId.endTokenId}"
        }
        return "$label,$joinedPathContexts"
    }
}
