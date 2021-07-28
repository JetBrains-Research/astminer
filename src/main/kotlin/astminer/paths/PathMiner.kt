package astminer.paths

import astminer.common.model.ASTPath
import astminer.common.model.Node

data class PathRetrievalSettings(val maxLength: Int, val maxWidth: Int)

class PathMiner(private val settings: PathRetrievalSettings) {
    private val pathWorker = PathWorker()

    fun retrievePaths(tree: Node): Collection<ASTPath> =
        pathWorker.retrievePaths(tree, settings.maxLength, settings.maxWidth)
}
