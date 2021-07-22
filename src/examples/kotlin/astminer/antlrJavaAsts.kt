package astminer

import astminer.config.*
import astminer.pipeline.Pipeline

/**
 * Retrieve ASTs from Java files, using ANTLR parser and save them in JSON format.
 */
fun antlrJavaAsts() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples/",
        outputDir = "examples_output/antlr_java_asts_json_storage",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Java)),
        labelExtractor = FileNameExtractorConfig(),
        storage = JsonAstStorageConfig(),
    )

    Pipeline(config).run()
}

fun main() {
    antlrJavaAsts()
}
