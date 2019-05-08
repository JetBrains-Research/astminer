package astminer.common.storage

import astminer.common.OrientedNodeType
import java.io.File

fun <T> dumpIdStorage(storage: IncrementalIdStorage<T>,
                      typeHeader: String,
                      csvSerializer: (T) -> String,
                      file: File) {
    val lines = mutableListOf("id,$typeHeader")

    storage.idPerItem.forEach {
        val id = it.value
        val item = it.key
        lines.add("$id,${csvSerializer.invoke(item)}")
    }

    writeLinesToFile(lines, file)
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
