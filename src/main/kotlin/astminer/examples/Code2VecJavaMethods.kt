package astminer.examples

import astminer.config.Code2VecPathStorageConfig
import astminer.config.FunctionPipelineConfig
import astminer.config.ParserConfig
import astminer.pipeline.getFunctionPipeline
import astminer.problem.FunctionNameProblem


//Retrieve paths from all Java files, using a GumTree parser.
//GumTreeMethodSplitter is used to extract individual method nodes from the compilation unit tree.
fun code2vecJavaMethods() {
    val folder = "src/test/resources/code2vecPathMining"
    val outputDir = "out_examples/code2vecPathMining"

    val pipelineConfig = FunctionPipelineConfig(
        folder,
        outputDir,
        ParserConfig(
            "antlr",
            listOf("java")
        ),
        emptyList(),
        FunctionNameProblem,
        Code2VecPathStorageConfig(
            maxPathLength = 5,
            maxPathWidth = 5
        )
    )

    val pipeline = getFunctionPipeline(pipelineConfig)

    pipeline.run()
}
