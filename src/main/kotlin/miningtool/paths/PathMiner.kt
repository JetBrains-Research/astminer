package miningtool.paths

import miningtool.common.ASTPath
import miningtool.common.Node

data class PathRetrievalSettings(val maxHeight: Int, val maxWidth: Int)

class PathMiner(val settings: PathRetrievalSettings) {
    private val pathWorker = PathWorker()

    fun retrievePaths(tree: Node): Collection<ASTPath> {
        return pathWorker.retrievePaths(tree, settings.maxHeight, settings.maxWidth)
    }
}