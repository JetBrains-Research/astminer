package astminer.pipeline

import astminer.common.model.FunctionInfo
import astminer.common.model.LanguageHandler
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.config.FilePipelineConfig
import astminer.config.FunctionPipelineConfig
import astminer.problem.LabeledResult

interface PipelineBranch {
    fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>>
}

class FilePipelineBranch(config: FilePipelineConfig) : PipelineBranch {
    private val filters = config.filters.map { it.filter }
    private val problem = config.problem.problem

    private fun ParseResult<out Node>.passesThroughFilters() = filters.all { filter -> filter.test(this) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> {
        val parseResult = languageHandler.parseResult
        return if (parseResult.passesThroughFilters()) {
            problem.process(parseResult)?.let { labeledResult ->  sequenceOf(labeledResult) } ?: emptySequence()
        } else {
            emptySequence()
        }
    }
}

class FunctionPipelineBranch(config: FunctionPipelineConfig) : PipelineBranch {
    private val filters = config.filters.map { it.filter }
    private val problem = config.problem.problem

    private fun FunctionInfo<out Node>.passesThroughFilters() = filters.all { filter -> filter.test(this) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> =
        languageHandler.splitIntoMethods().asSequence()
            .filter { functionInfo -> functionInfo.passesThroughFilters() }
            .mapNotNull { functionInfo -> problem.process(functionInfo) }
}