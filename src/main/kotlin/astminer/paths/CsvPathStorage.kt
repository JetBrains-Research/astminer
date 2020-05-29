package astminer.paths

import astminer.common.model.LabeledPathContextIds

class CsvPathStorage(outputFolderPath: String
) : CountingPathStorage<String>(outputFolderPath) {

    override fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<String>, tokensLimit: Long, pathsLimit: Long) {
        val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
            tokensMap.getIdRank(it.startTokenId) <= tokensLimit &&
                    tokensMap.getIdRank(it.endTokenId) <= tokensLimit &&
                    pathsMap.getIdRank(it.pathId) <= pathsLimit
        }.joinToString(separator = ";") { pathContextId ->
            "${pathContextId.startTokenId} ${pathContextId.pathId} ${pathContextId.endTokenId}"
        }
        labeledPathContextIdsWriter.println("${labeledPathContextIds.label},$pathContextIdsString")
    }
}
