package astminer.parse.treesitter.java

import astminer.config.FileExtension
import astminer.config.ParserType
import astminer.parse.ForeignParser
import java.io.File

class TreeSitterJavaParser : ForeignParser() {
    override val language: FileExtension = FileExtension.Java
    override val parser: ParserType = ParserType.TreeSitter

    override fun getArguments(file: File): List<String> =
        listOf("python3", "/tmp/tree_sitter/main.py", "-l", "java", "-f", file.path, "&")
}
