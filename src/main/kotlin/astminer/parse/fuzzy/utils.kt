package astminer.parse.fuzzy

import java.io.File
import java.util.concurrent.TimeUnit

fun String.runCommand(workingDir: File) {
    ProcessBuilder("/bin/sh", "-c", this)
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor(60, TimeUnit.MINUTES)
}

fun preprocessCppCode(inputFile: File, outputFile: File, preprocessCommand: String) = """
    grep '^\s*#\s*include' "${inputFile.absolutePath}" >__tmp_include.cpp
    grep -v '^\s*#\s*include\b' "${inputFile.absolutePath}" >__tmp_code.cpp
    touch __tmp_preprocessed.cpp
    if [ -s __tmp_code.cpp ] 
    then 
        $preprocessCommand __tmp_code.cpp | grep -v ^# >__tmp_preprocessed.cpp
    fi
    cat __tmp_include.cpp > "${outputFile.absolutePath}"
    cat __tmp_preprocessed.cpp >> "${outputFile.absolutePath}"
    rm __tmp_*.cpp
""".trimIndent()
