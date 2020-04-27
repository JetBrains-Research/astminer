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
    private var sourcePath: String = ""

    @Setup
    fun pathsSetup() {
        val classpath = System.getProperty("java.class.path")
        val classpathEntries: Array<String> = classpath.split(File.pathSeparator).toTypedArray()
        val astminerPath = classpathEntries[7].split("/build")[0]
        simpleFilePath = "$astminerPath/src/test/resources/testData/examples/1.java"
        longFilePath = "$astminerPath/longFileProject"
        if (isDirectoryEmpty(longFilePath)) {
            //downloading project...
        }
        if (isDirectoryEmpty(bigProjectPath)) {
            //downloading project...
        }
        bigProjectPath = "$astminerPath/intellij-idea"
        sourcePath = "$astminerPath/results"
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
        val args = listOf("--project", simpleFilePath, "--output", sourcePath)
        ProjectParser().main(args)
    }

    @Benchmark
    fun longFileProject() {
        val args = listOf("--project", longFilePath, "--output", sourcePath)
        ProjectParser().main(args)
    }

    @Benchmark
    fun bigProject() {
        val args = listOf("--project", bigProjectPath, "--output", sourcePath)
        ProjectParser().main(args)
    }
}