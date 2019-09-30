package astminer.parse.cpp

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

fun preprocessCppCode(file: File, outputDir: File, preprocessCommand: String) = """
    grep '^\s*#\s*include' ${file.absolutePath} >__tmp_include.cpp
    grep -v '^\s*#\s*include\b' ${file.absolutePath} >__tmp_code.cpp
    touch __tmp_preprocessed.cpp
    if [ -s __tmp_code.cpp ] 
    then 
        $preprocessCommand __tmp_code.cpp | grep -v ^# >__tmp_preprocessed.cpp
    fi
    cat __tmp_include.cpp >${outputDir.absolutePath}/${file.name}
    cat __tmp_preprocessed.cpp >>${outputDir.absolutePath}/${file.name}
    rm __tmp_*.cpp
""".trimIndent()