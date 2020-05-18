package astminer.cli

import astminer.parse.cpp.FuzzyCppParser
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import java.io.File

/**
 * Preprocess C/C++ project located in [projectRoot] and save the preprocessed files in [preprocessDir], replicating
 * structure of the original project.
 */
class ProjectPreprocessor : CliktCommand() {

    val projectRoot: String by option(
        "--project",
        help = "Path to the project that will be parsed"
    ).required()

    val preprocessDir: String by option(
        "--output",
        help = "Path to directory where the preprocessed data will be stored"
    ).required()

    private fun preprocessing() {
        val parser = FuzzyCppParser()
        parser.preprocessProject(File(projectRoot), File(preprocessDir))
    }

    override fun run() {
        preprocessing()
    }
}