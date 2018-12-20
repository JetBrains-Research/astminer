package miningtool.paths.storage

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