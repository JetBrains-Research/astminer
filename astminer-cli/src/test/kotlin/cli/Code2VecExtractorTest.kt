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

    private fun checkLanguageDir(languageDir: File) {
        languageDir.listFiles()?.forEach {
            println(it.name)
        }
    }

    private fun verifyParsingResult(extractedDataDir: File, languages: List<String>) {
        checkExtractedDir(extractedDataDir, languages)
        languages.forEach { language ->
            checkLanguageDir(extractedDataDir.resolve(language))
        }
    }

    @Test
    fun testDefaultExtraction() {
        val extractedDataDir = createTempDir("extractedData")
        val languages = listOf("java,py")
        val cliArgs = CliArgs.Builder(testDataDir, extractedDataDir)
            .extensions(languagesToString(languages))
            .build()

        code2VecExtractor.main(cliArgs.args)
        verifyParsingResult(extractedDataDir, languages)
    }
}


