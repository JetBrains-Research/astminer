@file:JvmName("PerformanceTest")

package astminer.performance

import astminer.common.model.LabeledPathContexts
import astminer.common.model.Node
import astminer.common.model.Parser
import astminer.parse.antlr.java.JavaParser
import astminer.parse.antlr.python.PythonParser
import astminer.paths.CsvPathStorage
import astminer.paths.PathMiner
import astminer.paths.PathRetrievalSettings
import astminer.paths.toPathContext
import java.io.File

fun <NodeType : Node, LangParser : Parser<NodeType>> langPerformanceTest(language: String,
                                                                         langSuffix: String,
                                                                         parser: LangParser,
                                                                         retrievalSettings: PathRetrievalSettings) {
    val startTime = System.currentTimeMillis()

    println("Running performance test for $language")

    val inputDir = "./testData/performanceTest/$langSuffix/"
    println("Using files in $inputDir")

    val miner = PathMiner(retrievalSettings)
    val outputDir = "out_examples/performanceTest$language"
    val storage = CsvPathStorage(outputDir)

    var filesNumber = 0L
    var filesFailed = 0L
    var locNumber = 0L
    var parsingElapsedTime = 0L
    var retrievingElapsedTime = 0L
    var storingElapsedTime = 0L

    File(inputDir).walkTopDown().filter { it.path.endsWith(".$langSuffix") }.forEach { file ->
        try {
            var currentTime = System.currentTimeMillis()
            val node = parser.parse(file.inputStream())
            parsingElapsedTime += System.currentTimeMillis() - currentTime
            if (node == null) return@forEach

            currentTime = System.currentTimeMillis()
            val paths = miner.retrievePaths(node)
            retrievingElapsedTime += System.currentTimeMillis() - currentTime

            currentTime = System.currentTimeMillis()
            storage.store(LabeledPathContexts(file.path, paths.map { toPathContext(it) }))
            storingElapsedTime += System.currentTimeMillis() - currentTime

            // If parsing is successful
            filesNumber++
            locNumber += file.readLines().size
        } catch (e: IllegalStateException) {
            println("Unable to parse ${file.path}")
            filesFailed++
        }
    }

    val currentTime = System.currentTimeMillis()
    storage.save()
    storingElapsedTime += System.currentTimeMillis() - currentTime

    println("Performance test took ${(System.currentTimeMillis() - startTime) / 1000} sec")
    println("Processed $filesNumber files, $locNumber lines of code")
    println("Failed to parse $filesFailed files")
    println()
    println("Parsing overall time: ${parsingElapsedTime / 1000} sec")
    println("Parsing speed: ${filesNumber * 1000 / parsingElapsedTime} files/sec, " +
            "${locNumber * 1000 / parsingElapsedTime} loc/sec")
    println()
    println("Path retrieval overall time: ${retrievingElapsedTime / 1000} sec")
    println("Path retrieval speed: ${filesNumber * 1000 / retrievingElapsedTime} files/sec, " +
            "${locNumber * 1000 / retrievingElapsedTime} loc/sec")
    println()
    println("Storing overall time: ${storingElapsedTime / 1000} sec")
    println("Storing speed: ${filesNumber * 1000 / storingElapsedTime} files/sec, " +
            "${locNumber * 1000 / storingElapsedTime} loc/sec")
    println()
}

fun main() {
    val retrievalSettings = PathRetrievalSettings(5, 5)
    langPerformanceTest("Python", "py", PythonParser(), retrievalSettings)
    langPerformanceTest("Java", "java", JavaParser(), retrievalSettings)
}
