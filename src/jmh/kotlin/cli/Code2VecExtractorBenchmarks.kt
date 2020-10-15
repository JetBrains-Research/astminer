package cli

import org.openjdk.jmh.annotations.*
import astminer.cli.*

@State(Scope.Benchmark)
open class Code2VecExtractorBenchmarks {

    private val defaultArgs = listOf("--split-tokens", "--granularity", "method", "--lang", "java")

    @Setup
    fun pathsSetup() {
        BenchmarksSetup().setup()
    }

    @Benchmark
    fun simpleProject() {
        val args = listOf("--project", BenchmarksSetup().simpleProjectPath,
                "--output", BenchmarksSetup().simpleProjectResultsPath) + defaultArgs
        Code2VecExtractor().main(args)
    }

    @Benchmark
    fun longFileProject() {
        val args = listOf("--project", BenchmarksSetup().longFilePath,
                "--output", BenchmarksSetup().longFileResultsPath) + defaultArgs
        Code2VecExtractor().main(args)
    }

    @Benchmark
    fun bigProject() {
        val args = listOf("--project", BenchmarksSetup().bigProjectPath,
                "--output", BenchmarksSetup().bigProjectResultsPath) + defaultArgs
        Code2VecExtractor().main(args)
    }
}