package cli

import java.io.File

data class BenchmarkResult(val taskName: String, val projectName: String) {
    var totalTime: Float = 0f
    var timeStd: Float = 0f
    var allocatedMemoryRate: Float = 0f
    var allocatedMemoryRateStd: Float = 0f
}

enum class MemoryMeasure {
    GB,
    MB
}

class BenchmarkResultWorker {
    object  TableFields {
        val taskToCsvField = hashMapOf(
            "Code2Vec" to "cli.Code2VecExtractorBenchmarks",
            "PathContext" to "cli.PathContextsExtractorBenchmarks",
            "ProjectParseCSV" to "cli.ProjectParserCsvBenchmarks",
            "ProjectParseDOT" to "cli.ProjectParserDotBenchmarks"
        )
        val projectToCsvField = hashMapOf(
            "Long file" to "longFileProject",
            "Small project (Gradle)" to "simpleProject",
            "Big project (IntelliJ IDEA)" to "bigProject"
        )
    }

    private val tasks = listOf("Code2Vec", "PathContext", "ProjectParseCSV", "ProjectParseDOT")
    private val projects = listOf("Long file", "Small project (Gradle)", "Big project (IntelliJ IDEA)")

    private fun convertMegabytes(megabytes: Float): Pair<Float, MemoryMeasure> {
        if (megabytes < 1024)
            return megabytes to MemoryMeasure.MB
        return megabytes / 1024 to MemoryMeasure.GB
    }

    fun parseCsvFile(pathToCsvFile: String): Map<Pair<String, String>, BenchmarkResult> {
        val taskToResult = hashMapOf<Pair<String, String>, BenchmarkResult>()
        tasks.forEach {task ->
            projects.forEach { project ->
                taskToResult[task to project] = BenchmarkResult(task, project)
            }
        }

        File(pathToCsvFile).forEachLine { line ->
            val csvFields = line.split(',')
            val taskName = csvFields[0].drop(1).dropLast(1)
            val resultValue = csvFields[4].toFloatOrNull() ?: 0f
            val resultStd = csvFields[5].toFloatOrNull() ?: 0f
            TableFields.taskToCsvField.entries.forEach { task ->
                TableFields.projectToCsvField.entries.forEach { project ->
                    val correctCsvField = "${task.value}.${project.value}"
                    if (taskName == correctCsvField) {
                        taskToResult[task.key to project.key]?.let {
                            it.totalTime = resultValue
                            it.timeStd = resultStd
                        }
                    } else if (taskName == "$correctCsvField:·gc.alloc.rate") {
                        taskToResult[task.key to project.key]?. let {
                            it.allocatedMemoryRate = resultValue
                            it.allocatedMemoryRateStd = resultStd
                        }
                    }
                }
            }
        }
        return taskToResult
    }

    fun saveToMarkdown(results: Map<Pair<String, String>, BenchmarkResult>, pathToMarkdownFile: String) {
        val outputFileWriter = File(pathToMarkdownFile).printWriter()
        outputFileWriter.println("| | ${projects.joinToString(" | ")} |")
        outputFileWriter.println("| --- |${"--- | ".repeat(projects.size)}")
        tasks.forEach { task ->
            outputFileWriter.print("| $task (time) |")
            projects.forEach { project ->
                val totalTime = "%.2f".format(results[task to project]?.totalTime)
                val timeStd = "%.2f".format(results[task to project]?.timeStd)
                outputFileWriter.print(" $totalTime ± $timeStd sec |")
            }
            outputFileWriter.print("\n")
            outputFileWriter.print("| $task (allocated memory per sec) |")
            projects.forEach { project ->
                val totalMemory = convertMegabytes(results[task to project]?.allocatedMemoryRate ?: 0f)
                val memoryStd = convertMegabytes(results[task to project]?.allocatedMemoryRateStd ?: 0f)
                outputFileWriter.print(
                        " ${"%.2f".format(totalMemory.first)} ${totalMemory.second.name.toLowerCase()} ± " +
                        "${"%.2f".format(memoryStd.first)} ${memoryStd.second.name.toLowerCase()} |"
                )
            }
            outputFileWriter.print("\n")
            if (task != tasks.last())
                outputFileWriter.println("| | ${" | ".repeat(projects.size)}")
        }
        outputFileWriter.close()
    }
}


fun main(args: Array<String>) {
    val benchmarkResultWorker = BenchmarkResultWorker()
    val results = benchmarkResultWorker.parseCsvFile("src/jmh/benchmarks.csv")
    benchmarkResultWorker.saveToMarkdown(results, "src/jmh/results.md")
}