package astminer.paths

import astminer.common.model.PathContextId

class CsvPathStorage(outputFolderPath: String,
                     pathsLimit: Long = Long.MAX_VALUE,
                     tokensLimit: Long = Long.MAX_VALUE
) : CountingPathStorage<String>(outputFolderPath, pathsLimit, tokensLimit) {

    override val separator: String = ";"

    override fun pathContextIdToString(pathContextId: PathContextId): String {
        return "${pathContextId.startTokenId} ${pathContextId.pathId} ${pathContextId.endTokenId}"
    }

    override fun pathContextToString(pathContextIdsString: String, label: String): String {
        return "$label,$pathContextIdsString"
    }
}
