package astminer.config

import astminer.filters.FileFilter
import astminer.filters.Filter
import astminer.filters.FunctionFilter
import astminer.problem.FileLevelProblem
import astminer.problem.FunctionLevelProblem
import astminer.problem.Problem

sealed class PipelineConfig {
    abstract val inputDir: String
    abstract val outputDir: String
    abstract val parser: ParserConfig
    abstract val problem: Problem<*>
    abstract val filters: List<Filter<*>>
    abstract val storage: StorageConfig
}

data class FilePipelineConfig(
    override val inputDir: String,
    override val outputDir: String,
    override val parser: ParserConfig,
    override val problem: FileLevelProblem,
    override val filters: List<FileFilter>,
    override val storage: StorageConfig
) : PipelineConfig()

data class FunctionPipelineConfig(
    val inputDir: String,
    val outputDir: String,
    val parser: ParserConfig,
    val problem: FunctionLevelProblem,
    val filters: List<FunctionFilter>,
    val storage: StorageConfig
)

data class ParserConfig(
    val type: String,
    val extensions: List<String>,
    val preprocess: Boolean
)

