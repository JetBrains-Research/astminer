package astminer

import astminer.examples.*

// TODO: remove main as we have CLI and example there
fun main(args: Array<String>) {
    runExamples()
}

fun runExamples() {
    code2vecJavaMethods()
    allJavaFilesGumTree()
    allJavaFiles()
    allJavaMethods()
    allPythonFiles()
    allCppFiles()

    AllJavaFiles.runExample()

    allJavaAsts()
}