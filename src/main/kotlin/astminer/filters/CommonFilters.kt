package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.common.splitToSubtokens
import astminer.featureextraction.NumberOfNodes

/**
 * Filter that excludes trees which do not satisfy [minSize] <= tree size <= [maxSize]
 * @param minSize The minimum size of trees that pass the filter
 * @param maxSize The maximum size of trees that pass the filter. Set it to null if there should be no upper bound.
 */
class TreeSizeFilter(private val minSize: Int = 0, private val maxSize: Int? = null) : FileFilter, FunctionFilter {
    private fun Node.treeSize() = NumberOfNodes.compute(this)

    private fun validateTree(root: Node): Boolean =
        minSize <= root.treeSize() && (maxSize == null || root.treeSize() <= maxSize)

    override fun validate(functionInfo: FunctionInfo<out Node>): Boolean = validateTree(functionInfo.root)

    override fun validate(parseResult: ParseResult<out Node>): Boolean = validateTree(parseResult.root)
}

/**
 * Filter that excludes trees that have more words than [maxWordsNumber] in any token of their node.
 */
class WordsNumberFilter(private val maxWordsNumber: Int) : FunctionFilter, FileFilter {
    private fun validateTree(root: Node) =
        !root.preOrder().any { node -> splitToSubtokens(node.token).size > maxWordsNumber }

    override fun validate(functionInfo: FunctionInfo<out Node>) = validateTree(functionInfo.root)

    override fun validate(parseResult: ParseResult<out Node>) = validateTree(parseResult.root)
}
