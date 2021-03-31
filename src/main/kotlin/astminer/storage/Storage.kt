package astminer.storage

import astminer.common.model.Node

interface Storage {
    val outputDirectoryPath: String

    fun store(labellingResult: LabellingResult<out Node>)
    fun close()
}
