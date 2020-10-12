package astminer.common.storage

import astminer.common.model.OrientedNodeType
import java.io.File

fun <T> dumpIdStorageToCsv(storage: RankedIncrementalIdStorage<T>,
                           typeHeader: String,
                           csvSerializer: (T) -> String,
                           file: File,
                           limit: Long = Long.MAX_VALUE) {
    file.printWriter().use { out ->
        out.println("id,$typeHeader")
        storage.idPerItem.forEach {
            val id = it.value
            val item = it.key
            if (storage.getKeyRank(item) <= limit) {
                out.println("$id,${csvSerializer.invoke(item)}")
            }
        }
    }
}

fun writeLinesToFile(lines: Collection<String>, file: File) {
    file.printWriter().use { out ->
        lines.forEach { out.println(it) }
    }
}

val tokenToCsvString: (String) -> String = { token -> token }

val nodeTypeToCsvString: (String) -> String = { nodeType -> nodeType }

val orientedNodeToCsvString: (OrientedNodeType) -> String = { nt -> "${nt.typeLabel} ${nt.direction}" }

val pathToCsvString: (List<Long>) -> String = { path -> path.joinToString(separator = " ") }
