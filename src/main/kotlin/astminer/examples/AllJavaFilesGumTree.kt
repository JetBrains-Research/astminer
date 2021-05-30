package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.config.*
import astminer.parse.gumtree.java.GumTreeJavaParser
import astminer.pipeline.Pipeline
import astminer.storage.path.Code2VecPathStorage
import astminer.storage.path.PathBasedStorageConfig
import java.io.File

//Retrieve paths from Java files, using a GumTree parser.
fun allJavaFilesGumTree() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/gumTreeMethodSplitter/",
        outputDir = "out_examples/allJavaFilesGumTree",
        parser = ParserConfig(ParserType.GumTree, listOf(FileExtension.Java)),
        problem = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )
    Pipeline(config).run()
}
