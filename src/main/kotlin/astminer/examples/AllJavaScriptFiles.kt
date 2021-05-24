package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.parse.antlr.javascript.JavaScriptParser
import astminer.storage.path.PathBasedStorageConfig
import astminer.storage.path.Code2VecPathStorage
import java.io.File

fun allJavaScriptFiles() {
    val folder = "src/test/resources/examples"
    val outputDir = "out_examples/allJavaScriptFilesAntlr"

    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    val files = getProjectFilesWithExtension(File(folder), "js")
    JavaScriptParser().parseFiles(files) { parseResult ->
        storage.store(parseResult.labeledWithFilePath())
    }

    storage.close()
}