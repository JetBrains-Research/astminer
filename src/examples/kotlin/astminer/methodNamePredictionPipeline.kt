package astminer

import astminer.config.*
import astminer.pipeline.Pipeline

/**
 * Prepare data for training code2vec model for method name prediction task.
 * Target language is Java, using ANTLR parser.
 */
fun methodNamePredictionPipeline() {
    val inputDir = "src/test/resources/examples"
    val outputDir = "examples_output/method_name_prediction_code2vec"

    val pipelineConfig = PipelineConfig(
        inputDir = inputDir,
        outputDir = outputDir,
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Java)),
        labelExtractor = FunctionNameExtractorConfig(),
        storage = Code2VecPathStorageConfig(
            maxPathLength = 5,
            maxPathWidth = 5
        )
    )

    Pipeline(pipelineConfig).run()
}

fun main() {
    methodNamePredictionPipeline()
}
