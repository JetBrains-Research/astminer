package astminer.problem

/**
 * Label extractors that have [File] granularity process and extract labels from *files*.
 * Label extractors that have [Function] granularity process and extract labels from *functions*
 *      (that are collected from files).
 */
enum class Granularity {
    File,
    Function
}

interface LabelExtractor {
    val granularity: Granularity
}
