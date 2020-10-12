package astminer.common

import me.vovak.antlr.parser.Java8Lexer
import me.vovak.antlr.parser.Java8Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

/**
 * Counts the number of lines in a given file skipping empty lines.
 * @param file file in which the number of lines is counted
 * @return number of lines in a given file
 */
fun numberOfLines(file: File): Int {
    return file.readLines().filter { it != "" }.size
}

/**
 * Changes extension of a given file to the new one.
 * @param file file which extension is changed
 * @param extension new extension of a given file
 * @return file with new extension
 */
fun changeExtensionTo(file: File, extension: String): File {
    val name = "${file.parent}/${file.nameWithoutExtension}.$extension"
    file.renameTo(File(name))
    return File(name)
}

/**
 * Adds class wrapper to a given file.
 * Class wrapper is necessary to be able to parse, for example, java files.
 * @param file file to which class wrapper is added
 * @param className class name using in wrapper
 */
fun addClassWrapper(file: File, className: String) {
    file.writeText("class $className {\n${file.readText()}\n}")
}

/**
 * Checks if java file has any syntax errors, that can be identified via [Java8Parser][me.vovak.antlr.parser.Java8Parser]
 * @param javaFile file which is checked for correct syntax
 * @return true if there are syntax errors and false otherwise
 */
fun hasSyntaxErrors(javaFile: File): Boolean {
    val lexer = Java8Lexer(CharStreams.fromStream(javaFile.inputStream()))
    val tokens = CommonTokenStream(lexer)
    val parser = Java8Parser(tokens)
    parser.compilationUnit()
    return parser.numberOfSyntaxErrors != 0
}

fun getProjectFiles(projectRoot: File, filter: (File) -> Boolean = { true }) = projectRoot
    .walkTopDown()
    .filter(filter)
    .toList()

fun getProjectFilesWithExtension(projectRoot: File, extension: String): List<File> =
    getProjectFiles(projectRoot) { it.isFile && it.extension == extension }