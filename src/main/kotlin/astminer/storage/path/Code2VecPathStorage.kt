package astminer.storage.path

import java.io.File

class Code2VecPathStorage(
        outputFolderPath: String,
        batchMode: Boolean = true,
        fragmentsPerBatch: Long = DEFAULT_FRAGMENTS_PER_BATCH
) : CountingPathStorage<String>(outputFolderPath, batchMode, fragmentsPerBatch) {

    override fun dumpPathContexts(file: File, tokensLimit: Long, pathsLimit: Long) {
        file.printWriter().use { out ->
            labeledPathContextIdsList.forEach { labeledPathContextIds ->
                val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
                    tokensMap.getIdRank(it.startTokenId) <= tokensLimit &&
                            tokensMap.getIdRank(it.endTokenId) <= tokensLimit &&
                            pathsMap.getIdRank(it.pathId) <= pathsLimit
                }.joinToString(separator = " ") { pathContextId ->
                    "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
                }
                out.println("${labeledPathContextIds.label} $pathContextIdsString")
            }
        }
    }

}
