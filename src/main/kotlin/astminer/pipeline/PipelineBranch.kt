package astminer.pipeline

import astminer.common.model.FunctionInfo
import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.config.*
import astminer.filters.*
import astminer.problem.*
import mu.KotlinLogging

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

private val logger = KotlinLogging.logger("PipelineBranch")

/**
 * PipelineBranch for pipeline with file-level granularity (FilePipelineConfig).
 * Works with files as a whole. Tests parsed files with filters and extracts a label from them.
 */
class FilePipelineBranch(config: PipelineConfig) : PipelineBranch {
    private val filters: List<FileFilter> = config.filters.mapNotNull { filterConfig ->
        when (filterConfig) {
            is TreeSizeFilterConfig -> TreeSizeFilter(filterConfig.maxTreeSize)
            is WordsNumberFilterConfig -> WordsNumberFilter(filterConfig.maxTokenWordsNumber)
            else -> {
                println("Filter `${filterConfig.serialName}` is not supported for this problem")
                logger.info { "Filter `${filterConfig.serialName}` is not supported for this problem" }
                null
            }
        }
    }

    private val problem: FileLevelProblem = when (config.problem) {
        is FileNameExtractorConfig -> FileNameExtractor
        is FolderNameExtractorConfig -> FolderNameExtractor
        else -> throw ProblemDefinitionException(Granularity.File, "FilePipelineBranch")
    }

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

/**
 * PipelineBranch for pipeline with function-level granularity (FunctionPipelineConfig).
 * Extracts functions from the parsed files.
 * Then tests functions with filters, processes them and extracts labels from each function.
 */
class FunctionPipelineBranch(config: PipelineConfig) : PipelineBranch {
    private val filters: List<FunctionFilter> = config.filters.mapNotNull { filterConfig ->
        when (filterConfig) {
            is TreeSizeFilterConfig -> TreeSizeFilter(filterConfig.maxTreeSize)
            is WordsNumberFilterConfig -> WordsNumberFilter(filterConfig.maxTokenWordsNumber)
            is ModifierFilterConfig -> ModifierFilter(filterConfig.modifiers)
            is AnnotationFilterConfig -> AnnotationFilter(filterConfig.annotations)
            is ConstructorFilterConfig -> ConstructorFilter
            is FunctionNameWordsNumberFilterConfig -> FunctionNameWordsNumberFilter(filterConfig.maxWordsNumber)
            else -> {
                println("Filter `${filterConfig.serialName}` is not supported for this problem")
                logger.info { "Filter `${filterConfig.serialName}` is not supported for this problem" }
                null
            }
        }
    }

    private val problem: FunctionLevelProblem = when (config.problem) {
        is FunctionNameProblemConfig -> FunctionNameProblem
        else -> throw ProblemDefinitionException(Granularity.Function, "FunctionPipelineBranch")
    }

    private fun passesThroughFilters(functionInfo: FunctionInfo<out Node>) =
        filters.all { filter -> filter.validate(functionInfo) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> =
        languageHandler.splitIntoFunctions().asSequence()
            .filter { functionInfo -> passesThroughFilters(functionInfo) }
            .mapNotNull { functionInfo -> problem.process(functionInfo) }
}

/**
 * This exception is thrown when problem granularity is implemented incorrectly or the problem is not specified
 * inside the correct pipeline branch.
 */
class ProblemDefinitionException(granularity: Granularity, branchName: String) :
    IllegalStateException(
        "The specified problem with granularity $granularity is not implemented inside of branch $branchName. " +
                "This should never happen!"
    )