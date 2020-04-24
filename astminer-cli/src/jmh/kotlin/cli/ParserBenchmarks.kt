package cli

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
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

    @Benchmark
    fun javaBenchmark(blackhole: Blackhole) {
        // instead of .... you need to put your full path to astminer,
        // because current plugin in gradle configuration does not support
        // customization of runtimePath. It run in /Users/username/.gradle/workers
        val projectPath = "..../astminer/astminer-cli/intellij-community"
        val sourcePath = "..../asrminer/astminer-cli/results"
        val args = listOf("--project", projectPath, "--output", sourcePath)
        blackhole.consume(ProjectParser().main(args))
    }
}