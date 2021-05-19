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
    val filters = config.filterConfigs.map { it.filter }
    val problem = config.problemConfig.problem

    private fun ParseResult<out Node>.passesThroughFilters() = filters.all { filter -> filter.isFiltered(this) }

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

class FunctionPipelineBranch(config: FunctionPipelineConfig) : PipelineBranch {
    val filters = config.filterConfigs.map { it.filter }
    val problem = config.problemConfig.problem

    private fun FunctionInfo<out Node>.passesThroughFilters() = filters.all { filter -> filter.isFiltered(this) }

    override fun process(languageHandler: LanguageHandler<out Node>): Sequence<LabeledResult<out Node>> =
        languageHandler.splitIntoMethods().asSequence()
            .filter { functionInfo -> functionInfo.passesThroughFilters() }
            .mapNotNull { functionInfo -> problem.process(functionInfo) }
}