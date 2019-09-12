package astminer.paths

import astminer.common.storage.*
import java.io.File

class CsvPathStorage : CountingPathStorage<String>() {

    override fun dumpPathContexts(file: File) {
        val lines = mutableListOf("label,path_contexts")
        labeledPathContextIdsList.forEach { labeledPathContextIds ->
            val pathContextIdsString = labeledPathContextIds.pathContexts.joinToString(separator = ";") { pathContextId ->
                "${pathContextId.startTokenId} ${pathContextId.pathId} ${pathContextId.endTokenId}"
            }
            lines.add("${labeledPathContextIds.label},$pathContextIdsString")
        }

        writeLinesToFile(lines, file)
    }

}