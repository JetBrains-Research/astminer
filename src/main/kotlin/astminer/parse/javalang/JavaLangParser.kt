package astminer.parse.javalang

import astminer.config.FileExtension
import astminer.config.ParserType
import astminer.parse.ForeignParser
import java.io.File

class JavaLangParser : ForeignParser() {
    override fun getArguments(file: File): List<String> {
        return listOf("python3", "src/main/python/parse/javalang/main.py", "-f", file.path, "&")
    }

    override val parser: ParserType = ParserType.JavaLang
    override val language: FileExtension = FileExtension.Java
}