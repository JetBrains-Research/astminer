package astminer

import astminer.config.*
import astminer.pipeline.Pipeline

/**
 * Retrieve paths from all JavaScript files using ANTLR parser.
 */
fun antlrJavaScriptPaths() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples",
        outputDir = "examples_output/antlr_javascript_paths",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.JavaScript)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )

    Pipeline(config).run()
}

fun main() {
    antlrJavaScriptPaths()
}
