package cli.util

import astminer.ast.CsvAstStorage
import astminer.ast.DotAstStorage
import astminer.common.model.AstStorage
import astminer.common.model.PathStorage
import astminer.paths.Code2VecPathStorage
import astminer.paths.CsvPathStorage

/**
 * @param astStorage class that implements ast's storage
 * @param type name of storage
 */
data class SupportedAstStorage(val astStorage: AstStorage, val type: String)

/**
 * List of supported AST storages and their parameter aliases.
 */
val supportedAstStorages = listOf(
    SupportedAstStorage(CsvAstStorage(), "csv"),
    SupportedAstStorage(DotAstStorage(), "dot")
)

fun getAstStorage(storageType: String): AstStorage {
    for (storage in supportedAstStorages) {
        if (storageType == storage.type) {
            return storage.astStorage
        }
    }
    throw UnsupportedOperationException("Unsupported AST storage $storageType")
}
//
///**
// * @param pbrStorage class that implements storage of path-based representations
// * @param type name of storage
// */
//data class SupportedPbrStorage<T>(val pbrStorage: PathStorage<T>, val type: String)
//
fun getPbrStorage(storageType: String, outputPath: String, batchMode: Boolean, batchSize: Long): PathStorage<String> {
    when (storageType) {
        "code2vec" -> return Code2VecPathStorage(outputPath, batchMode, batchSize)
        "csv" -> return CsvPathStorage(outputPath, batchMode, batchSize)
    }
    throw UnsupportedOperationException("Unsupported path-based representation storage type $storageType")
}
