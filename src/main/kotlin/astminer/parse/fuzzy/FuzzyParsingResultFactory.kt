package astminer.parse.fuzzy

import astminer.common.model.ParsingResult
import astminer.common.model.PreprocessingParsingResultFactory
import astminer.parse.fuzzy.cpp.FuzzyCppParser
import astminer.parse.fuzzy.cpp.FuzzyFunctionSplitter
import java.io.File

object FuzzyParsingResultFactory : PreprocessingParsingResultFactory {
    override fun parse(file: File): ParsingResult<FuzzyNode> {
        val actualFile = if (file.nameWithoutExtension.endsWith(preprocessSuffix)) {
            val actualFileNameSize = file.nameWithoutExtension.length - preprocessSuffix.length
            file.parentFile.resolve("${file.nameWithoutExtension.take(actualFileNameSize)}.${file.extension}")
        } else file
        return CppFuzzyParsingResult(actualFile)
    }

    /**
     * Run g++ preprocessor (with [preprocessCommand]) on a given file excluding 'include' directives.
     * The result of preprocessing is stored in create file "<filename>_preprocessed.cpp"
     * @param file file to preprocess
     *
     */
    override fun preprocess(file: File, outputDir: File?): File {
        if (file.extension !in supportedExtensions) return file
        val outputFile = outputDir?.resolve(file.name)
            ?: file.parentFile.resolve("${file.nameWithoutExtension}$preprocessSuffix.${file.extension}")
        preprocessCppCode(file, outputFile, preprocessCommand).runCommand(file.absoluteFile.parentFile)
        return outputFile
    }

    class CppFuzzyParsingResult(file: File) : ParsingResult<FuzzyNode>(file) {
        override val root = FuzzyCppParser().parseFile(file)
        override val splitter = FuzzyFunctionSplitter()
    }

    private val supportedExtensions = listOf("c", "cpp")
    private const val preprocessCommand: String = "g++ -E"
    private const val preprocessSuffix = "_preprocessed"
}
