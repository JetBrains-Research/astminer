package astminer.pipeline

import astminer.common.model.FunctionInfo
import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.config.FilePipelineConfig
import astminer.config.FunctionPipelineConfig
import astminer.problem.LabeledResult

/**
 * PipelineBranch is a part of the pipeline that can be completely different depending on the granularity (pipeline type)
 * It accepts parsed files (LanguageHandler) and returns labeled results.
 */
interface PipelineBranch {
    /**
     * Extracts labeled results from LanguageHandler
     * May mutate the AST.
     * Should have no other side-effects
     */
    fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>>
}

/**
 * PipelineBranch for pipeline with file-level granularity (FilePipelineConfig).
 * Works with files as a whole. Tests parsed files with filters and extracts a label from them.
 */
class FilePipelineBranch(config: FilePipelineConfig) : PipelineBranch {
    private val filters = config.filters.map { it.filter }
    private val problem = config.problem.problem

    private fun ParseResult<out Node>.passesThroughFilters() = filters.all { filter -> filter.test(this) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> {
        val parseResult = languageHandler.parseResult
        return if (parseResult.passesThroughFilters()) {
            val labeledResult = problem.process(parseResult) ?: return emptySequence()
            sequenceOf(labeledResult)
        } else {
            emptySequence()
        }
    }
}

/**
 * PipelineBranch for pipeline with function-level granularity (FunctionPipelineConfig).
 * Extracts functions from the parsed files.
 * Then tests functions with filters, processes them and extracts labels from each function.
 */
class FunctionPipelineBranch(config: FunctionPipelineConfig) : PipelineBranch {
    private val filters = config.filters.map { it.filter }
    private val problem = config.problem.problem

    private fun FunctionInfo<out Node>.passesThroughFilters() = filters.all { filter -> filter.test(this) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> =
        languageHandler.splitIntoFunctions().asSequence()
            .filter { functionInfo -> functionInfo.passesThroughFilters() }
            .mapNotNull { functionInfo -> problem.process(functionInfo) }
}
