package cli

import java.io.File

// How to start benchmark:
// 1. gradle daemons should be stopped before, so execute ./gradlew --stop
// 2. jmh plugin is unable to compile code incrementally, so execute ./gradlew clean
// 3. to run benchmarks execute ./gradlew jmh

const val warmUpIterations = 4
const val measurementIterations = 8
const val forkValue = 2

open class BenchmarksSetup() {

    private val cliPath = BenchmarksSetup::class.java.protectionDomain.codeSource.location.path.split("/build")[0]
    val simpleProjectPath: String = "$cliPath/src/jmh/resources/gradle"
    val simpleProjectResultsPath: String = "$cliPath/build/results/simpleProject"
    val longFilePath: String = "$cliPath/src/jmh/resources/LongJavaFile.java"
    val longFileResultsPath: String = "$cliPath/build/results/LongJavaFile"
    val bigProjectPath: String = "$cliPath/src/jmh/resources/intellij-community"
    val bigProjectResultsPath: String = "$cliPath/build/results/bigProject"
    private var lazyFlag = false

    fun setup() {
        if (lazyFlag) {
            return
        }
        val resourcesPath = "$cliPath/src/jmh/resources"
        if (isDirectoryEmpty(simpleProjectPath)) {
            val processBuilder = ProcessBuilder()
            processBuilder.command("git", "clone", "-d", "v6.3.0", "https://github.com/gradle/gradle")
                    .directory(File(resourcesPath))
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            assert(exitCode == 0)
        }
        if (isDirectoryEmpty(bigProjectPath)) {
            val processBuilder = ProcessBuilder()
            processBuilder.command("git", "clone", "-b", "idea/193.7288.8", "https://github.com/JetBrains/intellij-community")
                    .directory(File(resourcesPath))
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            assert(exitCode == 0)
        }
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