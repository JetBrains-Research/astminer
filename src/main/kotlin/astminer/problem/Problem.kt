package astminer.problem

/**
 * Problems that have [File] granularity process and extract labels from *files*.
 * Problems that have [Function] granularity process and extract labels from *functions* (that are collected from files).
 */
enum class Granularity {
    File,
    Function
}

interface Problem {
    val granularity: Granularity
}
