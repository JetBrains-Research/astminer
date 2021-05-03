package astminer.filters

import astminer.common.model.FunctionInfo
import astminer.common.model.Node
import astminer.common.model.ParseResult
import astminer.featureextraction.treeSize

abstract class TreeSizeFilter<T>(private val maxSize: Int) : Filter<T> {
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

class FileTreeSizeFilter(maxSize: Int) : TreeSizeFilter<ParseResult<out Node>>(maxSize), FileFilter {
    override val ParseResult<out Node>.tree: Node
        get() = root
}

class FunctionTreeSizeFilter(maxSize: Int) : TreeSizeFilter<FunctionInfo<out Node>>(maxSize),
    FunctionFilter {
    override val FunctionInfo<out Node>.tree: Node
        get() = root
}
