package astminer.parse.spoon

import astminer.common.model.Parser
import astminer.parse.ParsingException
import mu.KotlinLogging
import org.apache.commons.io.FileUtils.copyInputStreamToFile
import spoon.Launcher
import spoon.SpoonException
import java.io.File
import java.io.InputStream
import kotlin.io.path.Path

class SpoonJavaParser : Parser<SpoonNode> {
    // TODO try to run on different platforms
    private val tempPath = Path("./spoonTmpCanBeDeletedAfterParsing")
    private val suffix = ".java"
    private val logger = KotlinLogging.logger("Spoon parser")

    private fun createTempDirectory() {
        val directory = tempPath.toFile()
        if (!directory.exists()) { directory.mkdir() }
    }

    override fun parseInputStream(content: InputStream): SpoonNode {
        createTempDirectory()
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
    } catch (e:IllegalStateException) {
        throw ParsingException("Spoon", "Java", e)
    } catch (e: RuntimeException) {
        throw ParsingException("Spoon", "Java", e)
    }
}
