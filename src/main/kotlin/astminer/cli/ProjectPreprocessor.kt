package astminer.cli

import astminer.parse.cpp.FuzzyCppParser
import java.io.File

/**
 * Preprocess C/C++ project located in [projectRoot] and save the preprocessed files in [preprocessDir], replicating
 * structure of the original project.
 */
class ProjectPreprocessor {

    fun preprocessing() {
        FuzzyCppParser().preprocessProject(File(CliRunner().projectRoot), File(CliRunner().outputDir))
    }
}