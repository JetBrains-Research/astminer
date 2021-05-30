package astminer.pipeline.branch

import astminer.common.model.FunctionInfo
import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import astminer.config.*
import astminer.filters.*
import astminer.problem.*


/**
 * PipelineBranch for pipeline with function-level granularity (FunctionPipelineConfig).
 * Extracts functions from the parsed files.
 * Then tests functions with filters, processes them and extracts labels from each function.
 */
class FunctionPipelineBranch(config: PipelineConfig) : PipelineBranch {
    private val filters: List<FunctionFilter> = config.filters.map { filterConfig ->
        filterConfig.filterImpl as? FunctionFilter
            ?: throw IllegalFilterException(Granularity.Function, filterConfig.serialName)
    }

    private val problem: FunctionLabelExtractor = config.labelExtractor.labelExtractorImpl as? FunctionLabelExtractor
        ?: throw ProblemDefinitionException(Granularity.Function, config.labelExtractor.serialName)

    private fun passesThroughFilters(functionInfo: FunctionInfo<out Node>) =
        filters.all { filter -> filter.validate(functionInfo) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> =
        languageHandler.splitIntoFunctions().asSequence()
            .filter { functionInfo -> passesThroughFilters(functionInfo) }
            .mapNotNull { functionInfo -> problem.process(functionInfo) }
}

