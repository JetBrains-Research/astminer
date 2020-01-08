package cli.arguments

import cli.util.supportedLanguages
import com.github.ajalt.clikt.parameters.options.*

/**
 * Common arguments for commands requiring project parsing.
 */
abstract class ParseArgs : BaseArgs() {

    val extensions: List<String> by option(
        "--lang",
        help = "Comma-separated list of file extensions that will be parsed.\n" +
                "Supports 'c', 'cpp', 'java', 'py', defaults to all these extensions."
    ).split(",").default(supportedLanguages.map { it.extension })

    val granularityLevel: String by option(
        "--granularity",
        help = "Choose level of granularity ('file' or 'method', defaults to 'file')"
    ).default("file")

    val hideMethodName: Boolean by option(
        "--hide-method-name",
        help = "if passed with method level granularity, the names of all methods are replaced with placeholder token"
    ).flag(default = false)

    val splitTokens: Boolean by option(
        "--split-tokens",
        help = "if passed, split tokens into sequence of tokens"
    ).flag(default = false)
}
