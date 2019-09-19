package astminer.paths

import astminer.common.model.*
import astminer.common.storage.*
import java.io.File

abstract class CountingPathStorage<LabelType> : PathStorage<LabelType> {

    protected val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    protected val orientedNodeTypesMap: RankedIncrementalIdStorage<OrientedNodeType> = RankedIncrementalIdStorage()
    protected val pathsMap: RankedIncrementalIdStorage<List<Long>> = RankedIncrementalIdStorage()

    protected val labeledPathContextIdsList: MutableList<LabeledPathContextIds<LabelType>> = mutableListOf()

    private fun dumpTokenStorage(file: File, tokensLimit: Long) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file, tokensLimit)
    }

    private fun dumpOrientedNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(orientedNodeTypesMap, "node_type", orientedNodeToCsvString, file, Long.MAX_VALUE)
    }

    private fun dumpPathsStorage(file: File, pathsLimit: Long) {
        dumpIdStorageToCsv(pathsMap, "path", pathToCsvString, file, pathsLimit)
    }

    abstract fun dumpPathContexts(file: File, tokensLimit: Long, pathsLimit: Long)

    private fun doStore(pathContext: PathContext): PathContextId {
        val startTokenId = tokensMap.record(pathContext.startToken)
        val endTokenId = tokensMap.record(pathContext.endToken)
        val orientedNodesIds = pathContext.orientedNodeTypes.map { orientedNodeTypesMap.record(it) }
        val pathId = pathsMap.record(orientedNodesIds)
        return PathContextId(startTokenId, pathId, endTokenId)
    }

    override fun store(labeledPathContexts: LabeledPathContexts<LabelType>) {
        val labeledPathContextIds = LabeledPathContextIds(
                labeledPathContexts.label,
                labeledPathContexts.pathContexts.map { doStore(it) }
        )
        labeledPathContextIdsList.add(labeledPathContextIds)
    }

    override fun save(directoryPath: String) {
        save(directoryPath, Long.MAX_VALUE, Long.MAX_VALUE)
    }

    override fun save(directoryPath: String, pathsLimit: Long, tokensLimit: Long) {
        File(directoryPath).mkdirs()
        dumpTokenStorage(File("$directoryPath/tokens.csv"), tokensLimit)
        dumpOrientedNodeTypesStorage(File("$directoryPath/node_types.csv"))
        dumpPathsStorage(File("$directoryPath/paths.csv"), pathsLimit)

        dumpPathContexts(File("$directoryPath/path_contexts.csv"), tokensLimit, pathsLimit)
    }
}
