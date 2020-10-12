package astminer.paths

import astminer.common.model.ASTPath
import astminer.common.model.Node

data class PathRetrievalSettings(val maxLength: Int, val maxWidth: Int)

class PathMiner(val settings: PathRetrievalSettings) {
    private val pathWorker = PathWorker()

    fun retrievePaths(tree: Node): Collection<ASTPath> {
        return pathWorker.retrievePaths(tree, settings.maxLength, settings.maxWidth)
    }
}