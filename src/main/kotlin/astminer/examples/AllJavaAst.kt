package astminer.examples

import astminer.ast.VocabularyAstStorage
import astminer.parse.antlr.java.JavaParser
import java.io.File

//Retrieve paths from Java files, using a generated parser.
fun allJavaAsts() {
    val folder = "./testData/examples/"

    val storage = VocabularyAstStorage()

    File(folder).walkTopDown().filter { it.path.endsWith(".java") }.forEach { file ->
        val node = JavaParser().parse(file.inputStream()) ?: return@forEach
        storage.store(node, entityId = file.path)
    }

    storage.save("out_examples/allJavaAstsAntlr")
}