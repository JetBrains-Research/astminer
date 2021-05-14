package astminer.storage

import astminer.cli.LabeledResult
import astminer.common.model.Node
import java.io.Closeable

interface Storage : Closeable {
    val outputDirectoryPath: String

    fun store(labeledResult: LabeledResult<out Node>)

    fun store(labeledResults: Iterable<LabeledResult<out Node>>) {
        for (labeledResult in labeledResults) {
            store(labeledResult)
        }
    }
}
