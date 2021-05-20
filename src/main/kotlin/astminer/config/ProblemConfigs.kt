package astminer.config

import astminer.problem.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A config for problem that processes and extracts label from files
 */
@Serializable
sealed class FileProblemConfig {
    abstract val problem: FileLevelProblem
}

/**
 * @see FilePathExtractor
 */
@Serializable
@SerialName("label with filepath")
class FilePathExtractorConfig : FileProblemConfig() {
    @Transient
    override val problem = FilePathExtractor
}

/**
 * @see FolderExtractor
 */
@Serializable
@SerialName("label with folder name")
class FolderNameExtractorConfig : FileProblemConfig() {
    @Transient
    override val problem = FolderExtractor
}

/**
 * A config for problem that processes and extracts label from functions
 */
@Serializable
sealed class FunctionProblemConfig {
    abstract val problem: FunctionLevelProblem
}

/**
 * @see FunctionNameProblem
 */
@Serializable
@SerialName("function name prediction")
class FunctionNamePredictionConfig : FunctionProblemConfig() {
    @Transient
    override val problem = FunctionNameProblem
}
