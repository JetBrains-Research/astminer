package astminer

import astminer.config.*
import astminer.pipeline.Pipeline

/**
 * Retrieve paths from all Python files using ANTLR parser
 */
fun antlrPythonPaths() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples",
        outputDir = "examples_output/antlr_python_paths",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Python)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )

    Pipeline(config).run()
}

fun main() {
    antlrPythonPaths()
}
