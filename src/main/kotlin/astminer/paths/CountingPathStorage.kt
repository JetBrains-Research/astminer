package astminer.paths

import astminer.common.model.*
import astminer.common.storage.*
import java.io.File
import java.io.PrintWriter

abstract class CountingPathStorage<LabelType>(
        private val outputFolderPath: String) : PathStorage<LabelType> {

    private var contextsFileIndex = 0

    protected val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val orientedNodeTypesMap: RankedIncrementalIdStorage<OrientedNodeType> = RankedIncrementalIdStorage()
    protected val pathsMap: RankedIncrementalIdStorage<List<Long>> = RankedIncrementalIdStorage()

    private val pathsFile = File("$outputFolderPath/path_contexts.csv")
    protected val labeledPathContextIdsWriter: PrintWriter = PrintWriter(pathsFile)

    init {
        File(outputFolderPath).mkdirs()
        pathsFile.createNewFile()
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

    abstract fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<LabelType>, tokensLimit: Long, pathsLimit: Long)

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
        dumpPathContexts(labeledPathContextIds, Long.MAX_VALUE, Long.MAX_VALUE)
    }

    override fun save() {
        save(pathsLimit = Long.MAX_VALUE, tokensLimit = Long.MAX_VALUE)
    }

    override fun save(pathsLimit: Long, tokensLimit: Long) {
        dumpTokenStorage(File("$outputFolderPath/tokens.csv"), tokensLimit)
        dumpOrientedNodeTypesStorage(File("$outputFolderPath/node_types.csv"))
        dumpPathsStorage(File("$outputFolderPath/paths.csv"), pathsLimit)

        labeledPathContextIdsWriter.close()
    }
}
