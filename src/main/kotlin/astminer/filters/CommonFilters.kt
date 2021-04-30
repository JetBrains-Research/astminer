package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.featureextraction.treeSize

abstract class TreeSizeFilterPredicate<T>(private val maxSize: Int) : Filter<T> {
    private fun isTreeFiltered(root: Node): Boolean {
        return if (maxSize == -1) {
            true
        } else {
            root.treeSize() <= maxSize
        }
    }

    protected abstract val T.tree: Node

    override fun isFiltered(entity: T) = isTreeFiltered(entity.tree)
}

class FileTreeSizeFilterPredicate(maxSize: Int) : TreeSizeFilterPredicate<ParseResult<out Node>>(maxSize), FileFilter {
    override val ParseResult<out Node>.tree: Node
        get() = root
}

class FunctionTreeSizeFilterPredicate(maxSize: Int) : TreeSizeFilterPredicate<FunctionInfo<out Node>>(maxSize),
    FunctionFilter {
    override val FunctionInfo<out Node>.tree: Node
        get() = root
}
