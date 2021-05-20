package astminer.examples

import astminer.config.*
import astminer.pipeline.Pipeline


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun code2vecJavaMethods() {
    val folder = "src/test/resources/code2vecPathMining"
    val outputDir = "out_examples/code2vecPathMining"

    val pipelineConfig = FunctionPipelineConfig(
        inputDir = folder,
        outputDir = outputDir,
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Java)),
        problem = FunctionNamePredictionConfig(),
        storage = Code2VecPathStorageConfig(
            maxPathLength = 5,
            maxPathWidth = 5
        )
    )

    Pipeline(pipelineConfig).run()
}
