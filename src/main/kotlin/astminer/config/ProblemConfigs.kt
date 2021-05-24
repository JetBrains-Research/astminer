package astminer.config

import astminer.problem.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Problems that have [File] granularity process and extract labels from *whole files*.
 * Problems that have [Function] granularity process and extract labels from *functions* (that are collected from files).
 */
enum class Granularity {
    File,
    Function
}

@Serializable
sealed class ProblemConfig {
    abstract val granularity: Granularity
}

/**
 * @see FilePathExtractor
 */
@Serializable
@SerialName("file name")
object FileNameExtractorConfig : ProblemConfig() {
    override val granularity = Granularity.File
}

/**
 * @see FolderNameExtractor
 */
@Serializable
@SerialName("folder name")
object FolderNameExtractorConfig : ProblemConfig() {
    override val granularity = Granularity.File
}

/**
 * @see FunctionNameProblem
 */
@Serializable
@SerialName("function name")
object FunctionNameProblemConfig : ProblemConfig() {
    override val granularity = Granularity.Function
}
