package astminer.storage

import astminer.common.model.PathContextId

class Code2VecPathStorage(
    outputDirectoryPath: String,
    config: PathBasedStorageConfig,
    tokenProcessor: TokenProcessor = code2vecTokenProcessor
) :
    PathBasedStorage(outputDirectoryPath, config, tokenProcessor) {

    override fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String {
        val joinedPathContexts = pathContextIds.joinToString(" ") { pathContextId ->
            "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
        }
        return "$label $joinedPathContexts"
    }
}
