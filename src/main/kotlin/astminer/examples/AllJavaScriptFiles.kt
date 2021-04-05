package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.parse.antlr.javascript.JavaScriptParser
import astminer.storage.CsvPathStorage
import astminer.storage.PathBasedStorageConfig
import astminer.storage.labeledWithFilePath
import astminer.storage.splitTokenProcessor
import java.io.File

fun allJavaScriptFiles() {
    val folder = "src/test/resources/examples"
    val outputDir = "out_examples/allJavaScriptFilesAntlr"

    val storage = CsvPathStorage(outputDir, PathBasedStorageConfig(5, 5), splitTokenProcessor)

    val files = getProjectFilesWithExtension(File(folder), "js")
    JavaScriptParser().parseFiles(files) { parseResult ->
        parseResult.labeledWithFilePath()?.let {
            storage.store(it)
        }
    }

    storage.close()
}