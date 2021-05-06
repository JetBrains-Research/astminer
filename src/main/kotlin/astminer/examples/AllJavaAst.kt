package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.storage.ast.CsvAstStorage
import astminer.parse.antlr.java.JavaParser
import java.io.File

// Retrieve ASTs from Java files, using a generated parser.
fun allJavaAsts() {
    val folder = "src/test/resources/examples/"

    val storage = CsvAstStorage("out_examples/allJavaAstsAntlr")

    val files = getProjectFilesWithExtension(File(folder), "java")
    JavaParser().parseFiles(files) { parseResult ->
        storage.store(parseResult.labeledWithFilePath())
    }

    storage.close()
}