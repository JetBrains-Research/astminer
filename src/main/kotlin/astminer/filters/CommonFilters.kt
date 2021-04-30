package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.featureextraction.treeSize

class TreeSizeFilterPredicate(private val maxSize: Int) : MethodFilter, FileFilter {
    private fun isTreeFiltered(root: Node): Boolean {
        return if (maxSize == -1) {
            true
        } else {
            root.treeSize() <= maxSize
        }
    }

    override fun isFiltered(parseResult: ParseResult<out Node>) =
        if (parseResult.root != null) isTreeFiltered(parseResult.root) else false

    override fun isFiltered(functionInfo: FunctionInfo<out Node>) = isTreeFiltered(functionInfo.root)
}
