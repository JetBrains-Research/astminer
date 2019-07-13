package astminer.compare

import me.vovak.antlr.parser.Java8Lexer
import me.vovak.antlr.parser.Java8Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

fun numberOfLines(file: File) : Int {
    return file.readLines().filter { it != "" }.size
}

fun changeExtensionTo(file: File, extension: String) {
    file.renameTo(File("${file.parent}/${file.nameWithoutExtension}.$extension"))
}

fun addClassWrapper(file: File, className: String) {
    file.writeText("class $className { \n ${file.readText()} \n }")
}

fun hasSyntaxErrors(file: File) : Boolean {
    val lexer = Java8Lexer(CharStreams.fromStream(file.inputStream()))
    val tokens = CommonTokenStream(lexer)
    val parser = Java8Parser(tokens)
    parser.compilationUnit()
    return parser.numberOfSyntaxErrors != 0
}

fun makeFileParsable(file: File) {
    addClassWrapper(file, "A")
    if (hasSyntaxErrors(file)) file.delete()
}