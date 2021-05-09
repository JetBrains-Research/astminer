package astminer.examples

import astminer.config.*
import astminer.pipeline.getFunctionPipeline
import astminer.problem.FunctionNameProblem


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun code2vecJavaMethods() {
    val folder = "src/test/resources/code2vecPathMining"
    val outputDir = "out_examples/code2vecPathMining"

    val pipelineConfig = FunctionPipelineConfig(
        inputDir = folder,
        outputDir = outputDir,
        parserConfig = ParserConfig(
            "antlr",
            listOf("java")
        ),
        problemConfig = FunctionNamePredictionConfig(),
        storageCreatorConfig = Code2VecPathStorageCreatorConfig(
            maxPathLength = 5,
            maxPathWidth = 5
        )
    )

    val pipeline = getFunctionPipeline(pipelineConfig)

    pipeline.run()
}
