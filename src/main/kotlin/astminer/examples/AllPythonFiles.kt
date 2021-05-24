package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.config.*
import astminer.parse.antlr.python.PythonParser
import astminer.pipeline.Pipeline
import astminer.storage.path.PathBasedStorageConfig
import astminer.storage.TokenProcessor
import astminer.storage.path.Code2VecPathStorage
import java.io.File


fun allPythonFiles() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples",
        outputDir = "out_examples/allPythonFiles",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Python)),
        problem = FileNameExtractorConfig,
        storage = Code2VecPathStorageConfig(5, 5)
    )

    Pipeline(config).run()
}
