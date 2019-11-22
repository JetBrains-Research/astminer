package cli

import cli.util.CliArgs
import cli.util.languagesToString
import org.junit.Test
import java.io.File
import kotlin.test.assertTrue

internal class Code2VecExtractorTest {
    private val testDataDir = File("src/test/resources")
    private val code2VecExtractor = Code2VecExtractor()

    /**
     * Directory with extracted data should contain a directory for each specified language
     */
    private fun checkExtractedDir(extractedDataDir: File, languages: List<String>) {
        val metLanguages = mutableSetOf<String>()
        extractedDataDir.listFiles()?.forEach { file ->
            with(file) {
                assertTrue(isDirectory, "Extracted data directory should not contain file $name")
                assertTrue(languages.contains(file.name), "Unexpected directory $name")
                metLanguages.add(name)
            }
        }
        languages.forEach { language ->
            assertTrue(metLanguages.contains(language), "Did not find directory for $language")
        }
    }

    private fun validPathContextsFile(name: String, batching: Boolean): Boolean {
        return if (batching) {
            name.startsWith("path_contexts_") && name.endsWith(".csv")
        } else {
            name == "path_contexts.csv"
        }
    }

    private fun checkLanguageDir(languageDir: File, batching: Boolean) {
        val expectedFiles = listOf("tokens.csv", "paths.csv", "node_types.csv")
        languageDir.listFiles()?.forEach { file ->
            with(file) {
                assertTrue(
                    expectedFiles.contains(name) || validPathContextsFile(name, batching),
                    "Unexpected file $name in ${languageDir.name}"
                )
            }
        }
    }

    private fun verifyParsingResult(extractedDataDir: File, languages: List<String>, batching: Boolean) {
        checkExtractedDir(extractedDataDir, languages)
        languages.forEach { language ->
            checkLanguageDir(extractedDataDir.resolve(language), batching)
        }
    }

    @Test
    fun testDefaultExtraction() {
        val extractedDataDir = createTempDir("extractedData")
        val languages = listOf("java", "py")
        val cliArgs = CliArgs.Builder(testDataDir, extractedDataDir)
            .extensions(languagesToString(languages))
            .build()

        code2VecExtractor.main(cliArgs.args)
        verifyParsingResult(extractedDataDir, languages, true)
    }
}


