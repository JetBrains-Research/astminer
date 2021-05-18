package astminer.config

import astminer.problem.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class FileProblemConfig {
    abstract val problem: FileLevelProblem
}

@Serializable
@SerialName("label with filepath")
class FilePathExtractorConfig : FileProblemConfig() {
    @Transient
    override val problem = FilePathExtractor
}

@Serializable
@SerialName("label with folder name")
class FolderNameExtractorConfig : FileProblemConfig() {
    @Transient
    override val problem = FolderExtractor
}

@Serializable
sealed class FunctionProblemConfig {
    abstract val problem: FunctionLevelProblem
}

@Serializable
@SerialName("function name prediction")
class FunctionNamePredictionConfig : FunctionProblemConfig() {
    @Transient
    override val problem = FunctionNameProblem
}
