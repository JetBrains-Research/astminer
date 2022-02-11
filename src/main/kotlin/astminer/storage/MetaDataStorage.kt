package astminer.storage

import astminer.common.model.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.PrintWriter
import kotlin.io.path.Path

@Serializable
data class TreeMetaData(val label: String, val path: String, val range: NodeRange?) {
    constructor(labeledResult: LabeledResult<out Node>) : this(
        labeledResult.label,
        labeledResult.filePath,
        labeledResult.root.range
    )
}

class MetaDataStorage(override val outputDirectoryPath: String) : Storage {
    private val metadataWriters = mutableMapOf<DatasetHoldout, PrintWriter>()

    private fun DatasetHoldout.resolveHoldout(): PrintWriter {
        val newOutputFile = this.createDir(Path(outputDirectoryPath)).resolve(METADATA_FILENAME)
        newOutputFile.createNewFile()
        return PrintWriter(newOutputFile.outputStream(), true)
    }

    private fun PrintWriter.writeMetadata(labeledResult: LabeledResult<out Node>) {
        this.println(Json.encodeToString(TreeMetaData(labeledResult)))
    }

    override fun store(labeledResult: LabeledResult<out Node>, holdout: DatasetHoldout) {
        val writer = metadataWriters.getOrPut(holdout) { holdout.resolveHoldout() }
        writer.writeMetadata(labeledResult)
    }

    override fun close() {
        metadataWriters.values.forEach { it.close() }
    }

    companion object {
        const val METADATA_FILENAME = "metadata.jsonl"
    }
}
