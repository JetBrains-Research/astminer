package astminer.parse.javalang

import astminer.common.model.Parser
import astminer.config.FileExtension
import astminer.config.ParserType
import astminer.parse.ForeignParser
import astminer.parse.ParsingException
import astminer.parse.SimpleNode
import astminer.parse.getTreeFromPythonScript
import kotlinx.serialization.SerializationException
import org.apache.commons.io.FileUtils.copyInputStreamToFile
import java.io.File
import java.io.InputStream
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile

class JavaLangParser : ForeignParser() {
    override fun getArguments(file: File): Array<out String> {
        return arrayOf("python3", "src/main/python/parse/javalang/main.py", "-f", file.path, "&")
    }

    override val parser: ParserType = ParserType.JavaLang
    override val language: FileExtension = FileExtension.Java

//    override fun parseFile(file: File): SimpleNode {
//        return try {getTreeFromPythonScript("python3","src/main/python/parse/javalang/main.py","-f", file.path, "&")}
//        catch (e: SerializationException) {throw ParsingException(parserType = "JavaLang", language = "java", e)}
//    }
//
//    override fun parseInputStream(content: InputStream): SimpleNode {
//        throw ParsingException(parserType = "JavaLang", language = "java")
//    }
}