package astminer.paths

import astminer.common.*
import astminer.common.storage.*
import java.io.File

abstract class CountingPathStorage<LabelType> : PathStorage<LabelType> {

    private val tokensMap: IncrementalIdStorage<String> = IncrementalIdStorage()
    private val orientedNodeTypesMap: IncrementalIdStorage<OrientedNodeType> = IncrementalIdStorage()
    private val pathsMap: IncrementalIdStorage<List<Long>> = IncrementalIdStorage()

    protected val labeledPathContextIdsList: MutableList<LabeledPathContextIds<LabelType>> = mutableListOf()

    private fun dumpTokenStorage(file: File) {
        dumpIdStorageToCsv(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpOrientedNodeTypesStorage(file: File) {
        dumpIdStorageToCsv(orientedNodeTypesMap, "node_type", orientedNodeToCsvString, file)
    }

    private fun dumpPathsStorage(file: File) {
        dumpIdStorageToCsv(pathsMap, "path", pathToCsvString, file)
    }

    abstract fun dumpPathContexts(file: File)

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
        File(directoryPath).mkdirs()
        dumpTokenStorage(File("$directoryPath/tokens.csv"))
        dumpOrientedNodeTypesStorage(File("$directoryPath/node_types.csv"))
        dumpPathsStorage(File("$directoryPath/paths.csv"))

        dumpPathContexts(File("$directoryPath/path_contexts.csv"))
    }
}
