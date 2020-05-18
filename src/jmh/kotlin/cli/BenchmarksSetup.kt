package cli

import java.io.File


// How to start benchmark:
// 1. gradle daemons should be stopped before, so execute ./gradlew --stop
// 2. jmh plugin is unable to compile code incrementally, so execute ./gradlew clean
// 3. to run benchmarks execute ./gradlew jmh

open class BenchmarksSetup() {

    private val cliPath = BenchmarksSetup::class.java.protectionDomain.codeSource.location.path.split("/build")[0]
    val simpleProjectPath: String = "$cliPath/src/jmh/resources/gradle"
    val simpleProjectResultsPath: String = "$cliPath/build/results/simpleProject"
    val longFilePath: String = "$cliPath/src/jmh/resources/LongJavaFile.java"
    val longFileResultsPath: String = "$cliPath/build/results/LongJavaFile"
    val bigProjectPath: String = "$cliPath/src/jmh/resources/intellij-community"
    val bigProjectResultsPath: String = "$cliPath/build/results/bigProject"

    fun setup() {
        val resourcesPath = "$cliPath/src/jmh/resources"
        if (isDirectoryEmpty(simpleProjectPath)) {
            println("Gradle project is downloading for benchmark...")
            val exitCode = cloneGitProject("v6.3.0", "https://github.com/gradle/gradle", resourcesPath)
            if (exitCode != 0) {
                throw DownloadException("Error with downloading Gradle project!")
            }
        }
        if (isDirectoryEmpty(bigProjectPath)) {
            println("Intellij IDEA project is downloading for benchmark...")
            val exitCode = cloneGitProject("idea/193.7288.8", "https://github.com/JetBrains/intellij-community", resourcesPath)
            if (exitCode != 0) {
                throw DownloadException("Error with downloading Intellij IDEA project!")
            }
        }
    }

    private fun cloneGitProject(tag: String, projectLink: String, directory: String) : Int {
        val processBuilder = ProcessBuilder()
        processBuilder.command("git", "clone", "--depth", "1", "-b", tag, projectLink)
                .directory(File(directory))
        return processBuilder.start().waitFor()
    }

    private fun isDirectoryEmpty(path: String) : Boolean {
        val directory = File(path)
        return !directory.isDirectory || directory.list()?.isEmpty() ?: false
    }
}