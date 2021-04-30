package astminer.problem

import astminer.common.model.Node

interface Problem<T> {
    fun process(entity: T): LabeledResult<out Node>?
}
