package astminer.storage

import astminer.common.model.DatasetHoldout
import astminer.common.model.LabeledResult
import astminer.common.model.Node
import astminer.common.model.Storage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import java.io.PrintWriter
import kotlin.io.path.Path

const val METADATA_PATH_FIELD = "path"
const val METADATA_RANGE_FIELD = "range"
const val METADATA_FILENAME = "metadata.jsonl"

class MetaDataStorage(override val outputDirectoryPath: String) : Storage {
    private val metadataWriters = mutableMapOf<DatasetHoldout, PrintWriter>()

    private fun DatasetHoldout.resolveHoldout(): PrintWriter {
        val newOutputFile = this.createDir(Path(outputDirectoryPath)).resolve(METADATA_FILENAME)
        newOutputFile.createNewFile()
        return PrintWriter(newOutputFile.outputStream(), true)
    }

    private fun PrintWriter.writeMetadata(labeledResult: LabeledResult<out Node>) {
        val metadata = buildJsonObject {
            put("label", labeledResult.label)
            put(METADATA_RANGE_FIELD, Json.encodeToJsonElement(labeledResult.root.range))
            put(METADATA_PATH_FIELD, labeledResult.filePath)
        }
        this.println(Json.encodeToString(metadata))
    }

    override fun store(labeledResult: LabeledResult<out Node>, holdout: DatasetHoldout) {
        val writer = metadataWriters.getOrPut(holdout) { holdout.resolveHoldout() }
        writer.writeMetadata(labeledResult)
    }

    override fun close() {
        metadataWriters.values.forEach { it.close() }
    }
}
