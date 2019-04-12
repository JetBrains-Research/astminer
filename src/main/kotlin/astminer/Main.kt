package astminer

import astminer.examples.*

fun main(args: Array<String>) {
    runExamples()
}

fun runExamples() {
    allJavaFilesGumTree()
    allJavaFiles()
    allJavaMethods()
    allPythonFiles()

    AllJavaFiles.runExample()

    allJavaAsts()
}