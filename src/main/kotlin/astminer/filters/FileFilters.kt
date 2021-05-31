package astminer.filters

import astminer.common.model.Filter
import astminer.common.model.Node
import astminer.common.model.ParseResult

interface FileFilter : Filter {
    fun validate(parseResult: ParseResult<out Node>): Boolean
}
