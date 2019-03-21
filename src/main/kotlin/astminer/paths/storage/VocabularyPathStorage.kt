package astminer.paths.storage

import astminer.common.NodeType
import astminer.common.PathContext
import astminer.common.PathStorage
import java.io.File

class VocabularyPathStorage : PathStorage() {

    companion object {
        private val tokenToCsvString: (String) -> String = { token -> token }
        private val nodeTypeToCsvString: (NodeType) -> String = { nt -> "${nt.typeLabel} ${nt.direction}" }
        private val pathToCsvString: (List<Long>) -> String = { path -> path.joinToString(separator = " ") }
    }

    private val tokensMap: IncrementalIdStorage<String> = IncrementalIdStorage()
    private val nodeTypesMap: IncrementalIdStorage<NodeType> = IncrementalIdStorage()
    private val pathsMap: IncrementalIdStorage<List<Long>> = IncrementalIdStorage()

    private val pathContextsPerEntity: MutableMap<String, Collection<PathContextId>> = HashMap()

    data class PathContextId(val startTokenId: Long, val pathId: Long, val endTokenId: Long)

    private fun doStore(pathContext: PathContext): PathContextId {
        val startTokenId = tokensMap.record(pathContext.startToken)
        val endTokenId = tokensMap.record(pathContext.endToken)
        val nodeTypesIds = pathContext.nodeTypes.map { nodeTypesMap.record(it) }
        val pathId = pathsMap.record(nodeTypesIds)
        return PathContextId(startTokenId, pathId, endTokenId)
    }

    override fun store(pathContexts: Collection<PathContext>, entityId: String) {
        val pathContextIds = pathContexts.map { doStore(it) }
        pathContextsPerEntity[entityId] = pathContextIds
    }

    private fun dumpTokenStorage(file: File) {
        dumpIdStorage(tokensMap, "token", tokenToCsvString, file)
    }

    private fun dumpNodeTypesStorage(file: File) {
        dumpIdStorage(nodeTypesMap, "node_type", nodeTypeToCsvString, file)
    }

    private fun dumpPathsStorage(file: File) {
        dumpIdStorage(pathsMap, "path", pathToCsvString, file)
    }

    private fun dumpPathContexts(file: File) {
        val lines = mutableListOf("id,path_contexts")
        pathContextsPerEntity.forEach { id, pathContexts ->
            val pathContextsString = pathContexts.joinToString(separator = ";") { pathContextId ->
                "${pathContextId.startTokenId} ${pathContextId.pathId} ${pathContextId.endTokenId}"
            }
            lines.add("$id,$pathContextsString")
        }

        writeLinesToFile(lines, file)
    }

    override fun save(directoryPath: String) {
        File(directoryPath).mkdirs()
        dumpTokenStorage(File("$directoryPath/tokens.csv"))
        dumpNodeTypesStorage(File("$directoryPath/node_types.csv"))
        dumpPathsStorage(File("$directoryPath/paths.csv"))

        dumpPathContexts(File("$directoryPath/path_contexts.csv"))
    }
}