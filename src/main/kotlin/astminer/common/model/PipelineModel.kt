package astminer.common.model

import java.io.Closeable
import java.io.File

interface Filter

interface LabelExtractor

interface FileFilter : Filter {
    fun validate(parseResult: ParsingResult<out Node>): Boolean
}

interface FunctionFilter : Filter {
    fun validate(functionInfo: FunctionInfo<out Node>): Boolean
}

interface FileLabelExtractor : LabelExtractor {
    fun process(parseResult: ParsingResult<out Node>): LabeledResult<out Node>?
}

interface FunctionLabelExtractor : LabelExtractor {
    fun process(functionInfo: FunctionInfo<out Node>): LabeledResult<out Node>?
}

/**
 * An AST subtree with a label and the path of the source file.
 * @property root The root of the AST subtree.
 * @property label Any label for this subtree.
 * @property filePath The path to the source file where the AST is from.
 */
data class LabeledResult<T : Node>(val root: T, val label: String, val filePath: String)

fun <T : Node> ParsingResult<T>.labeledWith(label: String): LabeledResult<T> = LabeledResult(root, label, file.path)

/**
 * Storage saved labeled results to disk in a specified format.
 * Storage might extract any data from labeled result.
 * For instance, it might extract paths from trees
 */
interface Storage : Closeable {
    val outputDirectoryPath: String

    fun store(labeledResult: LabeledResult<out Node>, holdout: DatasetHoldout = DatasetHoldout.None)

    fun storeSynchronously(labeledResult: LabeledResult<out Node>, holdout: DatasetHoldout = DatasetHoldout.None) {
        synchronized(this) {
            store(labeledResult, holdout)
        }
    }

    fun store(labeledResults: Iterable<LabeledResult<out Node>>, holdout: DatasetHoldout = DatasetHoldout.None) {
        for (labeledResult in labeledResults) {
            store(labeledResult, holdout)
        }
    }

    fun storeSynchronously(
        labeledResults: Iterable<LabeledResult<out Node>>,
        holdout: DatasetHoldout = DatasetHoldout.None
    ) = synchronized(this) {
        store(labeledResults, holdout)
    }
}

enum class DatasetHoldout(val dirName: String) {
    Train("train"),
    Validation("val"),
    Test("test"),
    None("data");
}

/** Returns map with three entries (keys: train data pool, validation data pool and test data pool;
 *  values: holdout directories) if dataset structure is present.
 *  One pool (None) otherwise.**/
fun findDatasetHoldouts(inputDir: File): Map<DatasetHoldout, File> {
    val trainDir = inputDir.resolve(DatasetHoldout.Train.dirName)
    val valDir = inputDir.resolve(DatasetHoldout.Validation.dirName)
    val testDir = inputDir.resolve(DatasetHoldout.Test.dirName)

    return if (trainDir.exists() && valDir.exists() && testDir.exists()) {
        mapOf(
            DatasetHoldout.Train to trainDir,
            DatasetHoldout.Validation to valDir,
            DatasetHoldout.Test to testDir
        )
    } else {
        mapOf(
            DatasetHoldout.None to inputDir
        )
    }
}
