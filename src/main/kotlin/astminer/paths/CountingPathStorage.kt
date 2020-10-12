package astminer.paths

import astminer.common.model.*
import astminer.common.storage.*
import java.io.File
import java.io.PrintWriter

abstract class CountingPathStorage<LabelType>(override val directoryPath: String,
                                              override val tokensLimit: Long,
                                              override val pathsLimit: Long
) : PathStorage<LabelType> {

    protected val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    protected val orientedNodeTypesMap: RankedIncrementalIdStorage<OrientedNodeType> = RankedIncrementalIdStorage()
    protected val pathsMap: RankedIncrementalIdStorage<List<Long>> = RankedIncrementalIdStorage()

    private val pathsFile: File
    private val labeledPathContextIdsWriter: PrintWriter

    init {
        File(directoryPath).mkdirs()
        pathsFile = File("$directoryPath/path_contexts.csv")
        pathsFile.createNewFile()
        labeledPathContextIdsWriter = PrintWriter(pathsFile)
    }

    abstract fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: LabelType): String

    private fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<LabelType>) {
        val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
            tokensMap.getIdRank(it.startTokenId) <= tokensLimit &&
                    tokensMap.getIdRank(it.endTokenId) <= tokensLimit &&
                    pathsMap.getIdRank(it.pathId) <= pathsLimit
        }
        labeledPathContextIdsWriter.println(pathContextIdsToString(pathContextIdsString, labeledPathContextIds.label))
    }

    private fun storePathContext(pathContext: PathContext): PathContextId {
        val startTokenId = tokensMap.record(pathContext.startToken)
        val endTokenId = tokensMap.record(pathContext.endToken)
        val orientedNodesIds = pathContext.orientedNodeTypes.map { orientedNodeTypesMap.record(it) }
        val pathId = pathsMap.record(orientedNodesIds)
        return PathContextId(startTokenId, pathId, endTokenId)
    }

    override fun store(labeledPathContexts: LabeledPathContexts<LabelType>) {
        val labeledPathContextIds = LabeledPathContextIds(
                labeledPathContexts.label,
                labeledPathContexts.pathContexts.map { storePathContext(it) }
        )
        dumpPathContexts(labeledPathContextIds)
    }

    override fun close() {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, File("$directoryPath/tokens.csv"), tokensLimit)
        dumpIdStorageToCsv(orientedNodeTypesMap, "node_type", orientedNodeToCsvString, File("$directoryPath/node_types.csv"), Long.MAX_VALUE)
        dumpIdStorageToCsv(pathsMap, "path", pathToCsvString, File("$directoryPath/paths.csv"), pathsLimit)

        labeledPathContextIdsWriter.close()
    }
}
