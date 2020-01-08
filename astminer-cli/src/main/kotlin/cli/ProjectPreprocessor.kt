package cli

import astminer.parse.cpp.FuzzyCppParser
import cli.arguments.PreprocessorArgs
import java.io.File

/**
 * Preprocess C/C++ project located in [projectRoot] and save the preprocessed files in [outputRoot], replicating
 * structure of the original project.
 */
class ProjectPreprocessor : PreprocessorArgs() {

    private fun preprocessing() {
        val parser = FuzzyCppParser()
        parser.preprocessProject(File(projectRoot), File(outputRoot))
    }

    override fun run() {
        preprocessing()
    }
}