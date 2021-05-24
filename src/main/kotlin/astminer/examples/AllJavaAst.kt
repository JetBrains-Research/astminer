package astminer.examples

import astminer.common.getProjectFilesWithExtension
import astminer.config.*
import astminer.storage.ast.CsvAstStorage
import astminer.parse.antlr.java.JavaParser
import astminer.pipeline.Pipeline
import java.io.File

// Retrieve ASTs from Java files, using a generated parser.
fun allJavaAsts() {
    val config = PipelineConfig(
        inputDir = "src/test/resources/examples/",
        outputDir = "out_examples/allJavaAstsAntlr",
        parser = ParserConfig(ParserType.Antlr, listOf(FileExtension.Java)),
        problem = FileNameExtractorConfig,
        storage = CsvAstStorageConfig,
    )

    Pipeline(config).run()
}
