package cli.arguments

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*

/**
 * Common arguments for all the commands.
 */
abstract class BaseArgs : CliktCommand() {
    val projectRoot: String by option(
        "--project",
        help = "Path to the project that will be processed"
    ).required()

    val outputRoot: String by option(
        "--output",
        help = "Path to directory where the output data will be stored"
    ).required()
}
