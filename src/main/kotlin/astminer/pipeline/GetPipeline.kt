package astminer.pipeline

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.config.FilePipelineConfig
import astminer.config.FunctionPipelineConfig
import astminer.config.PipelineConfig

/**
 * Initializes the Pipeline given the [pipelineConfig].
 * This function must have no side effects!
 */
fun getPipeline(pipelineConfig: PipelineConfig): Pipeline<*> {
    return when (pipelineConfig) {
        is FilePipelineConfig -> getFilePipeline(pipelineConfig)
        is FunctionPipelineConfig -> getFunctionPipeline(pipelineConfig)
    }
}

fun getFilePipeline(filePipelineConfig: FilePipelineConfig): Pipeline<ParseResult<out Node>> =
    with(filePipelineConfig) {
        Pipeline(
            frontend = FilePipelineFrontend(inputDir, parserConfig.type, parserConfig.extensions),
            filters = filterConfigs.map { it.filter },
            problem = problemConfig.problem,
            excludedNodeTypes = excludedNodeTypes,
            storageFactory = storageFactoryConfig.getCreator(outputDir)
        )
    }

fun getFunctionPipeline(functionPipelineConfig: FunctionPipelineConfig): Pipeline<FunctionInfo<out Node>> =
    with(functionPipelineConfig) {
        Pipeline(
            frontend = FunctionPipelineFrontend(inputDir, parserConfig.type, parserConfig.extensions),
            filters = filterConfigs.map { it.filter },
            problem = problemConfig.problem,
            excludedNodeTypes = excludedNodeTypes,
            storageFactory = storageFactoryConfig.getCreator(outputDir)
        )
    }

