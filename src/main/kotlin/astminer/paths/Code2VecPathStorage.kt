package astminer.paths

import astminer.common.model.LabeledPathContextIds

class Code2VecPathStorage(outputFolderPath: String,
                          pathsLimit: Long = Long.MAX_VALUE,
                          tokensLimit: Long = Long.MAX_VALUE
) : CountingPathStorage<String>(outputFolderPath, pathsLimit, tokensLimit) {

    override fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<String>) {
        val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
            tokensMap.getIdRank(it.startTokenId) <= tokensLimit &&
                    tokensMap.getIdRank(it.endTokenId) <= tokensLimit &&
                    pathsMap.getIdRank(it.pathId) <= pathsLimit
        }.joinToString(" ") { pathContextId ->
            "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
        }
        labeledPathContextIdsWriter.println("${labeledPathContextIds.label} $pathContextIdsString")
    }
}
