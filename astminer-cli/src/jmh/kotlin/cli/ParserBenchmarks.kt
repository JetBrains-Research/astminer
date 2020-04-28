package cli

import org.openjdk.jmh.annotations.*
import java.io.File
import java.util.concurrent.TimeUnit


// How to start benchmark:
// 1. gradle daemons should be stopped before, so execute ./gradlew --stop
// 2. jmh plugin is unable to compile code incrementally, so execute ./gradlew clean
// 3. to run benchmarks execute ./gradlew jmh

@State(Scope.Benchmark)
@Warmup(iterations=4)
@Measurement(iterations=8)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(2)
open class ParserBenchmarks {

    private var simpleFilePath: String = ""
    private var longFilePath: String = ""
    private var bigProjectPath: String = ""
    private var simpleFileSourcePath: String = ""
    private var longFileSourcePath: String = ""
    private var bigProjectSourcePath: String = ""

    @Setup
    fun pathsSetup() {
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

    @Benchmark
    fun simpleProject() {
        val args = listOf("--project", simpleFilePath, "--output", simpleFileSourcePath)
        ProjectParser().main(args)
    }
    
    @Benchmark
    fun longFileProject() {
        val args = listOf("--project", longFilePath, "--output", longFileSourcePath)
        ProjectParser().main(args)
    }

    @Benchmark
    fun bigProject() {
        val args = listOf("--project", bigProjectPath, "--output", bigProjectSourcePath)
        ProjectParser().main(args)
    }
}