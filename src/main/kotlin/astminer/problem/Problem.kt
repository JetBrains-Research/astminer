package astminer.problem

import astminer.common.model.Node

/**
 * A structural element of the pipeline.
 * Extracts labels from entities and also may mutate them.
 */
interface Problem<T> {
    /**
     * Extracts label from entity.
     * If returns null then this entity will not be used further.
     */
    fun process(entity: T): LabeledResult<out Node>?
}
