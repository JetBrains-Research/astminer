package astminer

import java.io.File

fun checkExecutable(execName: String): Boolean {
    val execFolders = System.getenv("PATH").split(File.pathSeparator)
    execFolders.forEach {
        val folderFiles = File(it).list() ?: return@forEach
        if (folderFiles.contains(execName)) {
            return true
        }
    }
    return false
}