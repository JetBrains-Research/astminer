package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val inputDir = "src/test/resources/gumTreeMethodSplitter/"

    val outputDir = "out_examples/allJavaFilesGumTree"
    val storage = Code2VecPathStorage(outputDir, PathBasedStorageConfig(5, 5))

    val files = getProjectFilesWithExtension(File(inputDir), "java")
    GumTreeJavaParser().parseFiles(files) { parseResult ->
        storage.store(parseResult.labeledWithFilePath())
    }

    storage.close()
}
