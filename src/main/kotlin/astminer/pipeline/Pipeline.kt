package astminer.pipeline

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.preOrder
import astminer.filters.Filter
import astminer.problem.LabeledResult
import astminer.problem.Problem
import astminer.storage.Storage
import java.io.File

class Pipeline<T>(
    private val frontend: PipelineFrontend<T>,
    private val filters: List<Filter<T>>,
    private val problem: Problem<T>,
    private val excludedNodeTypes: List<String>,
    private val storage: Storage
) {

    private fun T.passesThroughFilters() = filters.all { filter -> filter.isFiltered(this) }

    private fun LabeledResult<out Node>.excludeNodes() {
        root.preOrder().forEach { node ->
            excludedNodeTypes.forEach { node.removeChildrenOfType(it) }
        }
    }

    fun run(files: List<File>) {
        val entities = frontend.parseEntities(files)

        val labeledResults = entities
            .filter { functionInfo -> functionInfo.passesThroughFilters() }
            .mapNotNull { problem.process(it) }

        for (labeledResult in labeledResults) {
            labeledResult.excludeNodes()
        }

        storage.store(labeledResults.asIterable())
    }
}