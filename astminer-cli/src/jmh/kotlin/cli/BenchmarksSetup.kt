package cli

import java.io.File

// How to start benchmark:
// 1. gradle daemons should be stopped before, so execute ./gradlew --stop
// 2. jmh plugin is unable to compile code incrementally, so execute ./gradlew clean
// 3. to run benchmarks execute ./gradlew jmh

const val warmUpIterations = 1
const val measurementIterations = 1
const val forkValue = 0

internal class BenchmarksSetup {

    var simpleProjectPath: String = ""
    var longFileProjectPath: String = ""
    var bigProjectPath: String = ""
    var sourcePath: String = ""
    var lazyFlag = false

    fun setup() {
        if (lazyFlag) {
            return
        }
        val classpath = System.getProperty("java.class.path")
        val classpathEntries: Array<String> = classpath.split(File.pathSeparator).toTypedArray()
        val astminerPath = classpathEntries[7].split("/build")[0]
        simpleProjectPath = "$astminerPath/src/jmh/resources/gradle"
        longFileProjectPath = "$astminerPath/src/jmh/resources/LongFileJavaProject"
        bigProjectPath = "$astminerPath/src/jmh/resources/intellij-community"
        sourcePath = "$astminerPath/build"
        lazyFlag = true
    }

    private fun isDirectoryEmpty(path :String) : Boolean {
        val directory = File(path)
        if (directory.isDirectory) {
            val files = directory.list()
            if (files != null && files.isNotEmpty())
                return false
        }
        return true
    }
}