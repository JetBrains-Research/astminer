package examples

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        throw Exception("Provide arguments to run the program!")
    }
    return when(args[0]) {
        "preprocess" -> ProjectPreprocessor().main(args.sliceArray(1 until args.size))
        "parse" -> ProjectParser().main(args.sliceArray(1 until args.size))
        else -> throw Exception("The first argument should be either 'preprocess' or 'parse'")
    }
}
