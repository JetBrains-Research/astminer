package astminer.common.model


/**
 * Stores path-contexts and their labels and saves them to directory.
 */
interface PathStorage<LabelType> {
    val directoryPath: String
    val tokensLimit: Long
    val pathsLimit: Long
    fun store(labeledPathContexts: LabeledPathContexts<LabelType>)
    fun close()
}

/**
 * Stores ASTs in form of their root and saves them to directory.
 */
interface AstStorage {
    val directoryPath: String
    fun store(root: Node, label: String) = store(root, label, "")
    fun store(root: Node, label: String, filePath: String)
    fun close()
}
