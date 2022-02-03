package astminer.storage.path

import astminer.common.model.LabeledPathContexts
import astminer.common.model.PathContext
import astminer.common.storage.RankedIncrementalIdStorage
import astminer.common.storage.dumpIdStorageToCsv
import java.io.File

class Code2SeqPathStorage(
    outputDirectoryPath: String,
    config: PathBasedStorageConfig,
    private val nodesToNumbers: Boolean = true
) : PathBasedStorage(outputDirectoryPath, config) {

    private val nodeTypesMap: RankedIncrementalIdStorage<String> = RankedIncrementalIdStorage()

    private fun pathContextToString(pathContext: PathContext): String {
        val stringNodeSequence = if (nodesToNumbers) {
            pathContext.orientedNodeTypes.joinToString("|") { nodeTypesMap.record(it.typeLabel).toString() }
        } else {
            pathContext.orientedNodeTypes.joinToString("|") { it.typeLabel }
        }
        return "${pathContext.startToken},$stringNodeSequence,${pathContext.endToken}"
    }

    override fun labeledPathContextsToString(labeledPathContexts: LabeledPathContexts<String>): String {
        val pathContexts = labeledPathContexts.pathContexts.map { pathContextToString(it) }
        return "${labeledPathContexts.label} ${pathContexts.joinToString(" ")}"
    }

    override fun close() {
        super.close()
        if (nodesToNumbers) {
            dumpIdStorageToCsv(
                nodeTypesMap,
                "node_type",
                { it },
                File("$outputDirectoryPath/node_types.csv")
            )
        }
    }
}
