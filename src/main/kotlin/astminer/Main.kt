package astminer

import astminer.cli.*

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("""
             You should specify the task as the first argument ("preprocess", "parse", "pathContexts", or "code2vec").
             For more information run `./cli.sh taskName --help`
        """.trimIndent())
    } else {
        return when (args[0]) {
            "preprocess" -> ProjectPreprocessor().main(args.sliceArray(1 until args.size))
            "parse" -> ProjectParser().main(args.sliceArray(1 until args.size))
            "pathContexts" -> PathContextsExtractor().main(args.sliceArray(1 until args.size))
            "code2vec" -> Code2VecExtractor().main(args.sliceArray(1 until args.size))
            else -> throw Exception("The first argument should be task's name: either 'preprocess', 'parse', 'pathContexts', or 'code2vec'")
        }
    }
}