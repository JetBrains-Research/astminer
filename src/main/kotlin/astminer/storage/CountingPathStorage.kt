package astminer.storage

import astminer.common.getNormalizedToken
import astminer.common.model.*
import astminer.common.storage.*
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import java.io.File
import java.io.PrintWriter

// TODO: finish the documentation
/**
 * Config for CountingPathStorage which contains several hyperparameters.
 * @property maxTokens ??
 * @property maxPaths ??
 * @property maxPathContextsPerEntity The maximum number of path contexts that should be extracted from LabeledParseResult.
 * In other words, the maximum number of path contexts to save from each file/method (depending on granularity)
 * @property maxPathLength The maximum length of a single path (based on the formal math definition of path length)
 * @property maxPathWidth The maximum width of a single path (based on the formal math definition of path width)
 */
data class CountingPathStorageConfig(
    val maxPathLength: Int,
    val maxPathWidth: Int,
    val normalizeToken: Boolean = true, // TODO: discuss this
    val maxTokens: Long = Long.MAX_VALUE,
    val maxPaths: Long = Long.MAX_VALUE,
    val maxPathContextsPerEntity: Int = Int.MAX_VALUE
)

/**
 * abstract Base class
 */
abstract class CountingPathStorage(
    final override val outputDirectoryPath: String,
    private val config: CountingPathStorageConfig
) : Storage {

    private val pathMiner = PathMiner(PathRetrievalSettings(config.maxPathLength, config.maxPathWidth))

    private val tokensMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()
    private val orientedNodeTypesMap: RankedIncrementalIdStorage<OrientedNodeType> = RankedIncrementalIdStorage()
    private val pathsMap: RankedIncrementalIdStorage<List<Long>> = RankedIncrementalIdStorage()

    private val pathsFile: File
    private val labeledPathContextIdsWriter: PrintWriter

    init {
        File(outputDirectoryPath).mkdirs()
        pathsFile = File("$outputDirectoryPath/path_contexts.csv")
        pathsFile.createNewFile()
        labeledPathContextIdsWriter = PrintWriter(pathsFile)
    }

    abstract fun pathContextIdsToString(pathContextIds: List<PathContextId>, label: String): String

    private fun dumpPathContexts(labeledPathContextIds: LabeledPathContextIds<String>) {
        val pathContextIdsString = labeledPathContextIds.pathContexts.filter {
            tokensMap.getIdRank(it.startTokenId) <= config.maxTokens &&
                    tokensMap.getIdRank(it.endTokenId) <= config.maxTokens &&
                    pathsMap.getIdRank(it.pathId) <= config.maxPaths
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

    private fun retrieveLabeledPathContexts(labellingResult: LabellingResult<out Node>): LabeledPathContexts<String> {
        val paths = pathMiner.retrievePaths(labellingResult.root).take(config.maxPathContextsPerEntity)
        return LabeledPathContexts(labellingResult.label, paths.map { astPath ->
            toPathContext(astPath) { node ->
                // TODO: maybe this whole hassle is not needed
                if (config.normalizeToken) {
                    node.getNormalizedToken()
                } else {
                    node.getToken()
                }
            }
        })
    }

    override fun store(labellingResult: LabellingResult<out Node>) {
        val labeledPathContexts = retrieveLabeledPathContexts(labellingResult)
        val labeledPathContextIds = LabeledPathContextIds(
            labeledPathContexts.label,
            labeledPathContexts.pathContexts.map { storePathContext(it) }
        )
        dumpPathContexts(labeledPathContextIds)
    }

    override fun close() {
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
            File("$outputDirectoryPath/node_types.csv"),
            Long.MAX_VALUE
        )
        dumpIdStorageToCsv(pathsMap, "path", pathToCsvString, File("$outputDirectoryPath/paths.csv"), config.maxPaths)

        labeledPathContextIdsWriter.close()
    }
}
