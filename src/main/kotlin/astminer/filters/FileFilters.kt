package astminer.filters

import astminer.common.model.Node
import astminer.common.model.ParseResult

interface FileFilter {
    fun test(parseResult: ParseResult<out Node>): Boolean
}
