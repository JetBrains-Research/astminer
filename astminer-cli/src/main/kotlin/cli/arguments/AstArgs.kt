package cli.arguments

import cli.util.supportedAstStorages
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option


/**
 * Arguments for 'parse' command.
 */
abstract class AstArgs() : ParseArgs() {
    val astStorageType: String by option(
        "--storage",
        help = "AST storage type ('dot' or 'csv', defaults to 'csv')"
    ).default(supportedAstStorages.first().type)
}
