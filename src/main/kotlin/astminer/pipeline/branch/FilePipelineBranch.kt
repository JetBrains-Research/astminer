package astminer.pipeline.branch

import astminer.common.model.*
import astminer.common.model.FileFilter

/**
 * PipelineBranch for pipeline with file-level granularity (FilePipelineConfig).
 * Works with files as a whole. Tests parsed files with filters and extracts a label from them.
 */
class FilePipelineBranch(
    filters: List<Filter>,
    private val labelExtractor: FileLabelExtractor
) : PipelineBranch {

    private val filters: List<FileFilter> = filters.map { filter ->
        filter as? FileFilter
            ?: throw IllegalFilterException("file", filter::class.simpleName)
    }

    private fun passesThroughFilters(parseResult: ParsingResult<out Node>) =
        filters.all { filter -> filter.validate(parseResult) }

    override fun process(parsingResult: ParsingResult<out Node>): List<LabeledResult<out Node>> {
        return if (passesThroughFilters(parsingResult)) {
            val labeledResult = labelExtractor.process(parsingResult) ?: return emptyList()
            listOf(labeledResult)
        } else {
            emptyList()
        }
    }
}
