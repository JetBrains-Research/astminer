package astminer.config

import astminer.problem.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class ProblemConfig {
    abstract val problemImplementation: Problem

    val granularity: Granularity
        get() = problemImplementation.granularity

    abstract val serialName: String
}

/**
 * @see FileNameExtractor
 */
@Serializable
@SerialName("file name")
class FileNameExtractorConfig : ProblemConfig() {
    @Transient
    override val problemImplementation = FileNameExtractor
    @Transient
    override val serialName = "file name"
}

/**
 * @see FolderNameExtractor
 */
@Serializable
@SerialName("folder name")
class FolderNameExtractorConfig : ProblemConfig() {
    @Transient
    override val problemImplementation = FolderNameExtractor
    @Transient
    override val serialName = "folder name"
}

/**
 * @see FunctionNameProblem
 */
@Serializable
@SerialName("function name")
class FunctionNameProblemConfig : ProblemConfig() {
    @Transient
    override val problemImplementation = FunctionNameProblem

    @Transient
    override val serialName = "function name"
}
