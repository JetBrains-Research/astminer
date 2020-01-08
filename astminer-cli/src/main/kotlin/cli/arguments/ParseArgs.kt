package cli.arguments

import cli.util.SupportedAstStorage
import cli.util.SupportedLanguage
import com.github.ajalt.clikt.parameters.options.*

/**
 * Arguments for 'parse' command.
 */
abstract class ParseArgs(
    supportedLanguages: List<SupportedLanguage>,
    defaultAstStorage: SupportedAstStorage
) : BaseArgs() {

    val extensions: List<String> by option(
        "--lang",
        help = "Comma-separated list of file extensions that will be parsed.\n" +
                "Supports 'c', 'cpp', 'java', 'py', defaults to all these extensions."
    ).split(",").default(supportedLanguages.map { it.extension })

    val astStorageType: String by option(
        "--storage",
        help = "AST storage type ('dot' or 'csv', defaults to 'csv')"
    ).default(defaultAstStorage.type)

    val granularityLevel: String by option(
        "--granularity",
        help = "Choose level of granularity ('file' or 'method', defaults to 'file')"
    ).default("file")

    val isMethodNameHide: Boolean by option(
        "--hide-method-name",
        help = "if passed with method level granularity, the names of all methods are replaced with placeholder token"
    ).flag(default = false)

    val isTokenSplitted: Boolean by option(
        "--split-tokens",
        help = "if passed, split tokens into sequence of tokens"
    ).flag(default = false)
}
