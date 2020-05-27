package astminer.examples

import astminer.ast.CsvAstStorage
import astminer.parse.antlr.java.JavaParser
import java.io.File

// Retrieve ASTs from Java files, using a generated parser.
fun allJavaAsts() {
    val folder = "./testData/examples/"

    val storage = CsvAstStorage(folder)

    File(folder).forFilesWithSuffix(".java") { file ->
        val node = JavaParser().parse(file.inputStream()) ?: return@forFilesWithSuffix
        storage.store(node, label = file.path)
    }

    storage.save()
}