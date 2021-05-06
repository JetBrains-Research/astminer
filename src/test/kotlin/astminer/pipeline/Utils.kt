package astminer.pipeline

import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import java.io.File



fun createTempDirectoryWithEmptyFiles(fileCounts: Map<String, Int>): File {
    val dir = createTempDir()
    for ((extension, fileCount) in fileCounts.entries) {
        for (i in 1..fileCount) {
            dir.resolve("$i.$extension").createNewFile()
        }
    }
    return dir
}

fun <T> getExtractedEntitiesCounts(entitiesFromFiles: Sequence<EntitiesFromFiles<T>>): Map<String, Int> =
    entitiesFromFiles.associate { it.fileExtension to it.entities.toList().size }
