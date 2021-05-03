package astminer.pipeline

import astminer.common.model.Node
import astminer.common.preOrder
import astminer.config.StorageConfig
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
    private val storageConfig: StorageConfig,
    private val outputDirectory: File
) {
    private fun T.passesThroughFilters() = filters.all { filter -> filter.isFiltered(this) }

    private fun LabeledResult<out Node>.excludeNodes() {
        root.preOrder().forEach { node ->
            excludedNodeTypes.forEach { node.removeChildrenOfType(it) }
        }
    }

    private fun getStorage(extension: String): Storage {
        val directoryForExtension = outputDirectory.resolve(extension)
        directoryForExtension.mkdir()

        return storageConfig.getStorage(directoryForExtension.path)
    }

    fun run() {
        for ((extension, entities) in frontend.getEntities()) {
            getStorage(extension).use { storage ->
                val labeledResults = entities
                    .filter { functionInfo -> functionInfo.passesThroughFilters() }
                    .mapNotNull { problem.process(it) }

                for (labeledResult in labeledResults) {
                    labeledResult.excludeNodes()
                }

                storage.store(labeledResults.asIterable())
            }
        }
    }
}