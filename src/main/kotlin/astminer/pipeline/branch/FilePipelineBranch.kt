package astminer.pipeline.branch

import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.config.*
import astminer.filters.FileFilter
import astminer.filters.Filter
import astminer.problem.*

/**
 * PipelineBranch for pipeline with file-level granularity (FilePipelineConfig).
 * Works with files as a whole. Tests parsed files with filters and extracts a label from them.
 */
class FilePipelineBranch(config: PipelineConfig) : PipelineBranch {
    private val filters: List<FileFilter> = config.filters.map { filterConfig ->
        filterConfig.filterImplementation as? FileFilter
            ?: throw IllegalFilterException(Granularity.File, filterConfig.serialName)
    }

    private val problem: FileLevelProblem = config.problem.problemImplementation as? FileLevelProblem
        ?: throw ProblemDefinitionException(Granularity.File, config.problem.serialName)

    private fun passesThroughFilters(parseResult: ParseResult<out Node>) =
        filters.all { filter -> filter.validate(parseResult) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> {
        val parseResult = languageHandler.parseResult
        return if (passesThroughFilters(parseResult)) {
            val labeledResult = problem.process(parseResult) ?: return emptySequence()
            sequenceOf(labeledResult)
        } else {
            emptySequence()
        }
    }
}