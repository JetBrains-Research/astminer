package astminer.paths

import astminer.common.model.PathContextId

class Code2VecPathStorage(outputFolderPath: String,
                          pathsLimit: Long = Long.MAX_VALUE,
                          tokensLimit: Long = Long.MAX_VALUE
) : CountingPathStorage<String>(outputFolderPath, pathsLimit, tokensLimit) {

    override fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String {
        val joinedPathContexts = pathContextIds.joinToString(" ") { pathContextId ->
            "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
        }
        return "$label $joinedPathContexts"
    }
}
