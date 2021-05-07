package astminer.config

import astminer.problem.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class FileProblemConfig {
    abstract val problem: FileLevelProblem
}

@Serializable
object FilePathExtractorConfig : FileProblemConfig() {
    @Transient
    override val problem = FilePathExtractor
}

@Serializable
object FolderNameExtractorConfig : FileProblemConfig() {
    @Transient
    override val problem = FolderExtractor
}

@Serializable
sealed class FunctionProblemConfig {
    abstract val problem: FunctionLevelProblem
}

@Serializable
object FunctionNamePredictionConfig : FunctionProblemConfig() {
    @Transient
    override val problem = FunctionNameProblem
}
