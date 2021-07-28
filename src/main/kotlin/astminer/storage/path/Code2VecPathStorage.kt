package astminer.storage.path

import astminer.common.model.*
import astminer.common.storage.*
import java.io.File

class Code2VecPathStorage(outputDirectoryPath: String, private val config: PathBasedStorageConfig) :
    PathBasedStorage(outputDirectoryPath, config) {

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val orientedNodeTypesMap: RankedIncrementalIdStorage<OrientedNodeType> = RankedIncrementalIdStorage()
    private val pathsMap: RankedIncrementalIdStorage<List<Long>> = RankedIncrementalIdStorage()

    private fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<String>): String {
        val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
            val isNumberOfTokensValid = config.maxTokens == null ||
                tokensMap.getIdRank(it.startTokenId) <= config.maxTokens &&
                tokensMap.getIdRank(it.endTokenId) <= config.maxTokens
            val isNumberOfPathsValid = config.maxPaths == null || pathsMap.getIdRank(it.pathId) <= config.maxPaths

            isNumberOfTokensValid && isNumberOfPathsValid
        }

        return pathContextIdsToString(pathContextIdsString, labeledPathContextIds.label)
    }

    private fun storePathContext(pathContext: PathContext): PathContextId {
        val startTokenId = tokensMap.record(pathContext.startToken)
        val endTokenId = tokensMap.record(pathContext.endToken)
        val orientedNodesIds = pathContext.orientedNodeTypes.map { orientedNodeTypesMap.record(it) }
        val pathId = pathsMap.record(orientedNodesIds)
        return PathContextId(startTokenId, pathId, endTokenId)
    }

    override fun labeledPathContextsToString(labeledPathContexts: LabeledPathContexts<String>): String {
        val labeledPathContextIds = LabeledPathContextIds(
            labeledPathContexts.label,
            labeledPathContexts.pathContexts.map { storePathContext(it) }
        )
        return dumpPathContexts(labeledPathContextIds)
    }

    private fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String {
        val joinedPathContexts = pathContextIds.joinToString(" ") { pathContextId ->
            "${pathContextId.startTokenId},${pathContextId.pathId},${pathContextId.endTokenId}"
        }
        return "$label $joinedPathContexts"
    }

    override fun close() {
        super.close()
        dumpIdStorageToCsv(
            tokensMap,
            "token",
            tokenToCsvString,
            File("$outputDirectoryPath/tokens.csv"),
            config.maxTokens
        )
        dumpIdStorageToCsv(
            orientedNodeTypesMap,
            "node_type",
            orientedNodeToCsvString,
            File("$outputDirectoryPath/node_types.csv")
        )
        dumpIdStorageToCsv(
            pathsMap,
            "path",
            pathToCsvString,
            File("$outputDirectoryPath/paths.csv"),
            config.maxPaths
        )
    }
}
