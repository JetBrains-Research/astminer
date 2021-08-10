package astminer

import astminer.config.*
import astminer.pipeline.Pipeline

/**
 * Retrieve paths from Java files, using a GumTree parser.
 */
fun gumTreeJavaPaths() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples",
        outputDir = "examples_output/gumtree_java_paths_kotlin_api",
        parser = ParserConfig(ParserType.GumTree, listOf(FileExtension.Java)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )
    Pipeline(config).run()
}

fun main() {
    gumTreeJavaPaths()
}
