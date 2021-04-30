package astminer.filters

import astminer.common.model.Node
import astminer.common.model.ParseResult

interface FileFilter: Filter<ParseResult<out Node>> {
    override fun isFiltered(entity: ParseResult<out Node>): Boolean
}
