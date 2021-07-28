package astminer

import astminer.config.*
import astminer.pipeline.Pipeline

/**
 * Preprocess .cpp files and retrieve paths from them, using a fuzzyc2cpg parser.
 */
fun fuzzyCppPathsWithPreprocessing() {
    val inputDir = "src/test/resources/examples"
    val outputDir = "examples_output/fuzzy_cpp_paths"

    // Pipeline will handle preprocessing automatically
    val config = PipelineConfig(
        inputDir = inputDir,
        outputDir = outputDir,
        parser = ParserConfig(ParserType.Fuzzy, listOf(FileExtension.Cpp)),
        labelExtractor = FileNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(5, 5)
    )

    Pipeline(config).run()
}

fun main() {
    fuzzyCppPathsWithPreprocessing()
}