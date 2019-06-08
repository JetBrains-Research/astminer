package astminer.examples

import java.io.File

fun iterateFiles(dir: File, condition: (File) -> Boolean, action: (File) -> Unit) {
    dir.walkTopDown().filter { it.isFile && condition(it) }.forEach { action.invoke(it) }
}

fun File.forFilesWithSuffix(extension: String, action: (File) -> Unit) {
    iterateFiles(this, ({ file: File -> file.path.endsWith(extension) }), action)
}