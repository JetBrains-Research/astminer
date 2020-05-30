package astminer.paths

import astminer.common.model.*
import astminer.common.storage.*
import java.io.File
import java.io.PrintWriter

abstract class CountingPathStorage<LabelType>(private val outputFolderPath: String,
                                              protected val tokensLimit: Long,
                                              protected val pathsLimit: Long
) : PathStorage<LabelType> {

    protected val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    protected val orientedNodeTypesMap: RankedIncrementalIdStorage<OrientedNodeType> = RankedIncrementalIdStorage()
    protected val pathsMap: RankedIncrementalIdStorage<List<Long>> = RankedIncrementalIdStorage()

    private val pathsFile: File
    protected val labeledPathContextIdsWriter: PrintWriter
    abstract val separator: String

    init {
        File(outputFolderPath).mkdirs()
        pathsFile = File("$outputFolderPath/path_contexts.csv")
        pathsFile.createNewFile()
        labeledPathContextIdsWriter = PrintWriter(pathsFile)
    }

    private fun dumpTokenStorage(file: File, tokensLimit: Long) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file, tokensLimit)
    }

    private fun dumpOrientedNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(orientedNodeTypesMap, "node_type", orientedNodeToCsvString, file, Long.MAX_VALUE)
    }

    private fun dumpPathsStorage(file: File, pathsLimit: Long) {
        dumpIdStorageToCsv(pathsMap, "path", pathToCsvString, file, pathsLimit)
    }

    abstract fun pathContextIdToString(pathContextId: PathContextId): String

    abstract fun pathContextToString(pathContextIdsString: String, label: LabelType): String

    private fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<LabelType>) {
        val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
            tokensMap.getIdRank(it.startTokenId) <= tokensLimit &&
                    tokensMap.getIdRank(it.endTokenId) <= tokensLimit &&
                    pathsMap.getIdRank(it.pathId) <= pathsLimit
        }.joinToString(separator){ pathContextId ->
            pathContextIdToString(pathContextId)
        }
        labeledPathContextIdsWriter.println(pathContextToString(pathContextIdsString, labeledPathContextIds.label))
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

    override fun save() {
        dumpTokenStorage(File("$outputFolderPath/tokens.csv"), tokensLimit)
        dumpOrientedNodeTypesStorage(File("$outputFolderPath/node_types.csv"))
        dumpPathsStorage(File("$outputFolderPath/paths.csv"), pathsLimit)

        labeledPathContextIdsWriter.close()
    }
}
