package cli

import java.io.File

// How to start benchmark:
// 1. gradle daemons should be stopped before, so execute ./gradlew --stop
// 2. jmh plugin is unable to compile code incrementally, so execute ./gradlew clean
// 3. to run benchmarks execute ./gradlew jmh

internal class BenchmarksSetup {

    var simpleFilePath: String = ""
    var longFilePath: String = ""
    var bigProjectPath: String = ""
    var simpleFileSourcePath: String = ""
    var longFileSourcePath: String = ""
    var bigProjectSourcePath: String = ""
    var lazyFlag = false

    fun setup() {
        if (lazyFlag) {
            return
        }
        val classpath = System.getProperty("java.class.path")
        val classpathEntries: Array<String> = classpath.split(File.pathSeparator).toTypedArray()
        val astminerPath = classpathEntries[7].split("/build")[0]
        simpleFilePath = "$astminerPath/src/test/resources/testData/examples/1.java"
        if (isDirectoryEmpty(longFilePath)) {
            println("Long java file is downloading for benchmark...")
            val processBuilder = ProcessBuilder()
            processBuilder.command("git", "clone", "https://github.com/foiki/LongFileJavaProject")
                    .directory(File(astminerPath)).start()
        }
        if (isDirectoryEmpty(bigProjectPath)) {
            println("Intellij IDEA Community is downloading for benchmark...")
            val processBuilder = ProcessBuilder()
            processBuilder.command("git", "clone", "https://github.com/JetBrains/intellij-community")
                    .directory(File(astminerPath)).start()
        }
        longFilePath = "$astminerPath/LongFileJavaProject"
        bigProjectPath = "$astminerPath/intellij-community"
        simpleFileSourcePath = "$astminerPath/benchmarkProduction/simpleProjectParse"
        longFileSourcePath = "$astminerPath/benchmarkProduction/longFileParse"
        bigProjectSourcePath = "$astminerPath/benchmarkProduction/bigProjectParse"
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