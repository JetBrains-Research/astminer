package cli.arguments

import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long

/**
 * Arguments for 'pathContexts' and 'code2vec' commands.
 */
abstract class PathBasedRepresentationArgs : BaseArgs() {
    val extensions: List<String> by option(
        "--lang",
        help = "File extensions that will be parsed"
    ).split(",").required()


    val maxPathHeight: Int by option(
        "--maxH",
        help = "Maximum height of path for code2vec"
    ).int().default(8)

    val maxPathWidth: Int by option(
        "--maxW",
        help = "Maximum width of path. " +
                "Note, that here width is the difference between token indices in contrast to the original code2vec."
    ).int().default(3)

    val maxPathContexts: Int by option(
        "--maxContexts",
        help = "Number of path contexts to keep from each method."
    ).int().default(500)

    val maxTokens: Long by option(
        "--maxTokens",
        help = "Keep only contexts with maxTokens most popular tokens."
    ).long().default(Long.MAX_VALUE)

    val maxPaths: Long by option(
        "--maxPaths",
        help = "Keep only contexts with maxTokens most popular paths."
    ).long().default(Long.MAX_VALUE)

    val batchMode: Boolean by option(
        "--batchMode",
        help = "Store path contexts in batches of `batchSize` to reduce memory usage. " +
                "If passed, limits on tokens and paths will be ignored!"
    ).flag(default = false)

    val batchSize: Long by option(
        "--batchSize",
        help = "Number of path contexts stored in each batch. Should only be used with `batchMode` flag."
    ).long().default(100)
}
