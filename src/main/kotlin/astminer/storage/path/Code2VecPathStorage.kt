package astminer.storage.path

import astminer.common.model.PathContextId

class Code2VecPathStorage(outputDirectoryPath: String, config: PathBasedStorageConfig) :
    PathBasedStorage(outputDirectoryPath, config) {

    override fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String {
        val joinedPathContexts = pathContextIds.joinToString(" ") { pathContextId ->
            "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
        }
        return "$label $joinedPathContexts"
    }
}
