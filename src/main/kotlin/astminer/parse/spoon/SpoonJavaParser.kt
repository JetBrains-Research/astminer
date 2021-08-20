package astminer.parse.spoon

import astminer.common.model.Parser
import astminer.parse.ParsingException
import mu.KotlinLogging
import org.apache.commons.io.FileUtils.copyInputStreamToFile
import spoon.Launcher
import spoon.SpoonException
import java.io.File
import java.io.InputStream
import kotlin.io.path.createTempDirectory

private val logger = KotlinLogging.logger("Spoon-JavaParser")

class SpoonJavaParser : Parser<SpoonNode> {
    // TODO try to run on different platforms
    private val tempPath = createTempDirectory(prefix = "spoonTmp").also { it.toFile().deleteOnExit() }
    private val suffix = ".java"
    private val logger = KotlinLogging.logger("Spoon parser")

    override fun parseInputStream(content: InputStream): SpoonNode {
        val tempFile = kotlin.io.path.createTempFile(suffix = suffix, directory = tempPath).toFile()
        copyInputStreamToFile(content, tempFile)

        val launcher = Launcher()
        launcher.addInputResource(tempFile.path)
        val model = try { launcher.buildModel() } catch (e: Exception) {
            logger.warn { "Error occured while parsing: $e" }
            throw e
        } finally { tempFile.delete() }

        return SpoonNode(model.unnamedModule, null)
    }

    override fun parseFile(file: File): SpoonNode = try {
        val launcher = Launcher()
        launcher.addInputResource(file.path)
        val model = launcher.buildModel()
        SpoonNode(model.unnamedModule, null)
    } catch (e: SpoonException) {
        throw ParsingException("Spoon", "Java", e)
    } catch (e: IllegalStateException) {
        throw ParsingException("Spoon", "Java", e)
    } catch (e: RuntimeException) {
        throw ParsingException("Spoon", "Java", e)
    } catch (e: StackOverflowError) {
        logger.error {
            "Got StackOverflowError while parsing ${file.path}. Please report about this issue." +
                "More about : $e"
        }
        throw ParsingException("Spoon", "Java")
    }
}
