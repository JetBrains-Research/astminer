package astminer

import astminer.parse.ParsingException

/**
 * Run all examples in one place.
 */
fun main() {
    // Java api example
    GumTreeJavaPaths.runExample()
    // Kotlin api examples
    antlrJavaAsts()
    antlrJavaMethodPaths()
    antlrJavaScriptPaths()
    antlrPythonPaths()
    collectFeatures()
    fuzzyCppPathsWithPreprocessing()
    gumTreeJavaMethodPaths()
    gumTreeJavaPaths()
    try { gumTreePythonMethodPaths() } catch (ex: ParsingException) { println("No python parser to run this example") }
    methodNamePredictionPipeline()
}