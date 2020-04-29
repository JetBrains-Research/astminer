package cli

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Warmup(iterations = warmUpIterations)
@Measurement(iterations = measurementIterations)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(forkValue)
open class PathContextsExtractorBenchmarks {

    @Setup
    fun pathsSetup() {
        BenchmarksSetup().setup()
    }

    @Benchmark
    fun simpleProject() {
        val args = listOf("--project", BenchmarksSetup().simpleFilePath,
                "--output", BenchmarksSetup().sourcePath)
        PathContextsExtractor().main(args)
    }

    @Benchmark
    fun longFileProject() {
        val args = listOf("--project", BenchmarksSetup().longFilePath,
                "--output", BenchmarksSetup().sourcePath)
        PathContextsExtractor().main(args)
    }

    @Benchmark
    fun bigProject() {
        val args = listOf("--project", BenchmarksSetup().bigProjectPath,
                "--output", BenchmarksSetup().sourcePath)
        PathContextsExtractor().main(args)
    }
}