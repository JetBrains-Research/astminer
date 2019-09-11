package astminer.parse.cpp

import java.io.File

fun File.readInOneLine(): String = readText().replace("\n", "")
