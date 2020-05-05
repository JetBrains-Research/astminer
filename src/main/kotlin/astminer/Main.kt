package astminer

import astminer.cli.*

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("""
             You should specify the task as the first argument ("preprocess", "parse", "pathContexts", or "code2vec").
             For more information run `./cli.sh taskName --help`
        """.trimIndent())
    } else {
        CliRunner().parseArgs(args)
    }
}