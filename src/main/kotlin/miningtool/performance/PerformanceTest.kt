@file:JvmName("PerformanceTest")

package miningtool.performance

import miningtool.common.Node
import miningtool.common.Parser
import miningtool.common.toPathContext
import miningtool.parse.antlr.java.JavaParser
import miningtool.parse.antlr.python.PythonParser
import miningtool.paths.PathMiner
import miningtool.paths.PathRetrievalSettings
import miningtool.paths.storage.VocabularyPathStorage
import java.io.File
import java.lang.IllegalStateException

fun <NodeType : Node, LangParser : Parser<NodeType>> langPerformanceTest(language: String,
                                                                         langSuffix: String,
                                                                         parser: LangParser,
                                                                         retrievalSettings: PathRetrievalSettings) {
    val startTime = System.currentTimeMillis()

    println("Running performance test for $language")

    val folder = "./testData/performanceTest/$langSuffix/"
    println("Using files in $folder")

    val miner = PathMiner(retrievalSettings)
    val storage = VocabularyPathStorage()

    var filesNumber = 0L
    var filesFailed = 0L
    var locNumber = 0L
    var parsingElapsedTime = 0L
    var retrievingElapsedTime = 0L
    var storingElapsedTime = 0L

    File(folder).walkTopDown().filter { it.path.endsWith(".$langSuffix") }.forEach { file ->
        try {
            var currentTime = System.currentTimeMillis()
            val node = parser.parse(file.inputStream())
            parsingElapsedTime += System.currentTimeMillis() - currentTime
            if (node == null) return@forEach

            currentTime = System.currentTimeMillis()
            val paths = miner.retrievePaths(node)
            retrievingElapsedTime += System.currentTimeMillis() - currentTime

            currentTime = System.currentTimeMillis()
            storage.store(paths.map { toPathContext(it) }, entityId = file.path)
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
    storage.save("out_examples/performanceTest$language")
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

fun main(args: Array<String>) {
    val retrievalSettings = PathRetrievalSettings(5, 5)
    langPerformanceTest("Python", "py", PythonParser(), retrievalSettings)
    langPerformanceTest("Java", "java", JavaParser(), retrievalSettings)
}