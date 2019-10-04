package astminer.paths

import astminer.common.model.*
import astminer.common.storage.*
import java.io.File

const val DEFAULT_FRAGMENTS_PER_BATCH = 100L

abstract class CountingPathStorage<LabelType>(
        private val outputFolderPath: String,
        val batchMode: Boolean = true,
        val fragmentsPerBatch: Long = DEFAULT_FRAGMENTS_PER_BATCH) : PathStorage<LabelType> {

    private var contextsFileIndex = 0
    private var currentFragmentsCount = 0

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

    private fun dumpPathContextsIfNeeded() {
        if (!batchMode || currentFragmentsCount < fragmentsPerBatch) {
            return
        }
        File(outputFolderPath).mkdirs()
        dumpPathContexts(File("$outputFolderPath/path_contexts_${contextsFileIndex++}.csv"),
                Long.MAX_VALUE, Long.MAX_VALUE)
        labeledPathContextIdsList.clear()
        currentFragmentsCount = 0
    }

    override fun store(labeledPathContexts: LabeledPathContexts<LabelType>) {
        val labeledPathContextIds = LabeledPathContextIds(
                labeledPathContexts.label,
                labeledPathContexts.pathContexts.map { doStore(it) }
        )
        labeledPathContextIdsList.add(labeledPathContextIds)
        currentFragmentsCount++


        dumpPathContextsIfNeeded()
    }

    override fun save() {
        save(pathsLimit = Long.MAX_VALUE, tokensLimit = Long.MAX_VALUE)
    }

    override fun save(pathsLimit: Long, tokensLimit: Long) {
        if (batchMode && (pathsLimit < Long.MAX_VALUE || tokensLimit < Long.MAX_VALUE)) {
            println("Ignoring path and token limit settings due to batchMode processing")
        }
        File(outputFolderPath).mkdirs()
        dumpTokenStorage(File("$outputFolderPath/tokens.csv"), tokensLimit)
        dumpOrientedNodeTypesStorage(File("$outputFolderPath/node_types.csv"))
        dumpPathsStorage(File("$outputFolderPath/paths.csv"), pathsLimit)

        if (!batchMode) {
            dumpPathContexts(File("$outputFolderPath/path_contexts.csv"), tokensLimit, pathsLimit)
        } else {
            dumpPathContexts(File("$outputFolderPath/path_contexts_${contextsFileIndex++}.csv"),
                    Long.MAX_VALUE, Long.MAX_VALUE)
        }
    }
}
