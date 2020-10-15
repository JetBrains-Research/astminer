# JMH benchmarks

This benchmark runs ASTMiner in several CLI modes. These arguments are used for each run:
- Code2Vec
```(bash)
code2vec --project <input_data_path> --output <output_path> --split-tokens --granularity method --lang java
```
- PathContext
```(bash)
pathContexts  --project <input_data_path> --output <output_path> --lang java
```
- Parse (CSV storage)
```(bash)
parse --project <input_data_path>  --output <output_path> --split-tokens --granularity method --lang java --storage csv
```
- Parse (DOT storage)
```(bash)
parse --project <input_data_path>  --output <output_path> --split-tokens --granularity method --lang java --storage dot
```

As data, we use 3 types of data:
1. Long file - long java file with ~5000 lines of code. It can be found in [resources](resources/LongJavaFile.java).
2. Small project - concrete version of [Gradle](https://github.com/gradle/gradle) project. We clone branch with tag `v6.3.0`.
3. Big project - concrete version of [Intellij Community](https://github.com/JetBrains/intellij-community) project. We clone branch with tag `idea/193.7288.8`.

## Results

Current results can be found in [results.md](results.md).

These results were on achieved on EC2 instance `i3.8xlarge`. Parameters can be bound in [build.gradle.kts](../../build.gradle.kts) file.

## How to run benchmarks

Steps to runs benchmarks:
1. gradle daemons should be stopped before, so execute `./gradlew --stop`
2. jmh plugin is unable to compile code incrementally, so execute `./gradlew clean`
3. to run benchmarks execute `./gradlew jmh`

After that you will found results in `build/reports/benchmarks.csv`.
You can convert these results into markdown table (like [results.md](results.md)) using [benchmark result worker](kotlin/cli/BenchmarkResultWorker.kt).
You should write needed paths in `main`, compile the file and run it. 


