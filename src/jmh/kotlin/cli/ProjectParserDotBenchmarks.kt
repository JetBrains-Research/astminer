package cli

import org.openjdk.jmh.annotations.*
import astminer.cli.*

@State(Scope.Benchmark)
open class ProjectParserDotBenchmarks {

    @Setup
    fun pathsSetup() {
        BenchmarksSetup().setup()
    }

    @Benchmark
    fun simpleProject() {
        val args = listOf("--project", BenchmarksSetup().simpleProjectPath,
                "--output", BenchmarksSetup().simpleProjectResultsPath,
                "--storage", "dot")
        ProjectParser().main(args)
    }

    @Benchmark
    fun longFileProject() {
        val args = listOf("--project", BenchmarksSetup().longFilePath,
                "--output", BenchmarksSetup().longFileResultsPath,
                "--storage", "dot")
        ProjectParser().main(args)
    }

    @Benchmark
    fun bigProject() {
        val args = listOf("--project", BenchmarksSetup().bigProjectPath,
                "--output", BenchmarksSetup().bigProjectResultsPath,
                "--storage", "dot")
        ProjectParser().main(args)
    }
}