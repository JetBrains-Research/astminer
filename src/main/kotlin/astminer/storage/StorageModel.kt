package astminer.storage

import astminer.common.model.LabeledPathContexts
import astminer.common.model.Node


/**
 * Stores path-contexts and their labels and saves them to directory.
 */
interface PathStorage<LabelType> {
    fun store(labeledPathContexts: LabeledPathContexts<LabelType>)
    fun save()
    fun save(pathsLimit: Long, tokensLimit: Long)
}

/**
 * Stores ASTs in form of their root and saves them to directory.
 */
interface AstStorage {
    fun store(root: Node, label: String)
    fun save()
}
