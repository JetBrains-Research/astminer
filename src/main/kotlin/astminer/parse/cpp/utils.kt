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

fun preprocessCppCode(file: File, outputDirName: String, preprocessCommand: String = "g++ -E") = """
    grep '^\s*#\s*include' ${file.name} >__tmp_include.cpp
    grep -Pv '^\s*#\s*include\b' ${file.name} >__tmp_code.cpp
    touch __tmp_preprocessed.cpp
    if [ -s __tmp_code.cpp ] 
    then 
        $preprocessCommand __tmp_code.cpp | grep -v ^# >__tmp_preprocessed.cpp
    fi
    mkdir -p $outputDirName
    cat __tmp_include.cpp >$outputDirName/${file.name}
    cat __tmp_preprocessed.cpp >>$outputDirName/${file.name}
    rm __tmp_*.cpp
""".trimIndent()