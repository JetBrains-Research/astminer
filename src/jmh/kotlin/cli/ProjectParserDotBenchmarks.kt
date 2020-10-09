package cli

import org.openjdk.jmh.annotations.*
import astminer.cli.*

@State(Scope.Benchmark)
open class ProjectParserDotBenchmarks {

    private val defaultArgs = listOf("--split-tokens", "--granularity", "method", "--lang", "java", "--storage", "dot")

    @Setup
    fun pathsSetup() {
        BenchmarksSetup().setup()
    }

    @Benchmark
    fun simpleProject() {
        val args = listOf("--project", BenchmarksSetup().simpleProjectPath,
                "--output", BenchmarksSetup().simpleProjectResultsPath) + defaultArgs
        ProjectParser().main(args)
    }

    @Benchmark
    fun longFileProject() {
        val args = listOf("--project", BenchmarksSetup().longFilePath,
                "--output", BenchmarksSetup().longFileResultsPath) + defaultArgs
        ProjectParser().main(args)
    }

    @Benchmark
    fun bigProject() {
        val args = listOf("--project", BenchmarksSetup().bigProjectPath,
                "--output", BenchmarksSetup().bigProjectResultsPath) + defaultArgs
        ProjectParser().main(args)
    }
}