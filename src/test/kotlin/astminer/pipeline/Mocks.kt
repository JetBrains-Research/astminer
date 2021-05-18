package astminer.pipeline

import astminer.common.DummyNode
import astminer.common.labeledWith
import astminer.common.model.Node
import astminer.filters.Filter
import astminer.problem.LabeledResult
import astminer.problem.Problem
import astminer.storage.Storage

class DummyPipelineFrontend(private val extensionsToNodeNames: Map<String, String>) : PipelineFrontend<DummyNode> {
    override fun getEntities(): Sequence<EntitiesFromFiles<DummyNode>> =
        extensionsToNodeNames.entries.map { (extension, nodeName) -> EntitiesFromFiles(extension, sequenceOf(DummyNode(nodeName))) }
            .asSequence()
}

class SimplePipelineFrontend(private val nodes: List<DummyNode>) : PipelineFrontend<DummyNode> {
    override fun getEntities(): Sequence<EntitiesFromFiles<DummyNode>> =
        sequenceOf(EntitiesFromFiles("", nodes.asSequence()))
}

class DummyFilter(private val excludeName: String = "") : Filter<DummyNode> {
    override fun isFiltered(entity: DummyNode): Boolean = entity.getToken() != excludeName
}


class DummyLabelExtractor(private val excludeName: String = "") : Problem<DummyNode> {
    override fun process(entity: DummyNode): LabeledResult<out Node>? = if (entity.data != excludeName) {
        entity.labeledWith("label ${entity.data}")
    } else {
        null
    }
}

class BambooLabelExtractor : Problem<DummyNode> {
    private fun getLabel(entity: Node): String {
        val firstChildLabel = entity.getChildren().firstOrNull()?.let { getLabel(it) } ?: ""
        return "${entity.getTypeLabel()}<$firstChildLabel"
    }

    override fun process(entity: DummyNode): LabeledResult<out Node> = entity.labeledWith(getLabel(entity))
}

class DummyStorageFactory : StorageFactory {
    private val storages = mutableMapOf<String, DummyStorage>()

    val results: Map<String, Set<String>>
        get() = storages.mapValues { (_, storage) -> storage.labeledResults }

    override fun createStorageAndOutputFolder(extension: String): Storage {
        val storage = DummyStorage()
        storages[extension] = storage
        return storage
    }
}

class DummyStorage : Storage {
    override val outputDirectoryPath: String = ""

    val labeledResults = mutableSetOf<String>()

    override fun store(labeledResult: LabeledResult<out Node>) {
        labeledResults.add(labeledResult.label)
    }

    override fun close() {
        /* no-op */
    }
}
