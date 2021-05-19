package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.featureextraction.treeSize

class TreeSizeFilter(private val maxSize: Int) : FileFilter, FunctionFilter {
    private fun testTree(root: Node): Boolean = root.treeSize() <= maxSize

    override fun test(functionInfo: FunctionInfo<out Node>): Boolean = testTree(functionInfo.root)

    override fun test(parseResult: ParseResult<out Node>): Boolean = testTree(parseResult.root)
}
