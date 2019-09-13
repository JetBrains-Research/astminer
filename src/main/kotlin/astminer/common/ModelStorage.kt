package astminer.common


/**
 * Stores path-contexts and their labels and saves them to directory.
 */
interface PathStorage<LabelType> {
    fun store(labeledPathContexts: LabeledPathContexts<LabelType>)
    fun save(directoryPath: String)
}

/**
 * Stores ASTs in form of their root and saves them to directory.
 */
interface AstStorage {
    fun store(root: Node, label: String)
    fun save(directoryPath: String)
}
