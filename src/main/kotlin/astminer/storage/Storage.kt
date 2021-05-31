package astminer.storage

import astminer.labelextractor.LabeledResult
import astminer.common.model.Node
import java.io.Closeable

/**
 * Storage saved labeled results to disk in a specified format.
 * Storage might extract any data from labeled result.
 * For instance, it might extract paths from trees
 */
interface Storage : Closeable {
    val outputDirectoryPath: String

    fun store(labeledResult: LabeledResult<out Node>)

    fun store(labeledResults: Iterable<LabeledResult<out Node>>) {
        for (labeledResult in labeledResults) {
            store(labeledResult)
        }
    }
}
