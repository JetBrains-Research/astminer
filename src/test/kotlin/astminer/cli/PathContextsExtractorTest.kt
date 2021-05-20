package astminer.cli

import astminer.cli.util.CliArgs
import astminer.cli.util.languagesToString
import astminer.cli.util.verifyPathContextExtraction
import org.junit.Test
import java.io.File
import java.nio.file.Files.createTempDirectory

internal class PathContextsExtractorTest {
    private val testDataDir = File("src/test/resources")
    private val pathContextsExtractor = PathContextsExtractor()

    @Test
    fun testDefaultExtraction() {
        val extractedDataDir = createTempDirectory("extractedData")
        val languages = listOf("java", "py")
        val cliArgs = CliArgs.Builder(testDataDir, extractedDataDir.toFile())
            .extensions(languagesToString(languages))
            .build()

        pathContextsExtractor.main(cliArgs.args)
        verifyPathContextExtraction(extractedDataDir.toFile(), languages, false)
    }
}