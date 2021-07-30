package astminer.parse.javaparser

import astminer.common.model.Parser
import astminer.parse.ParsingException
import com.github.javaparser.ParseProblemException
import com.github.javaparser.StaticJavaParser
import mu.KotlinLogging
import java.io.InputStream

val logger = KotlinLogging.logger("JavaParser")

class JavaParserParseWrapper : Parser<JavaParserNode> {
    override fun parseInputStream(content: InputStream): JavaParserNode {
        try {
            val tree = StaticJavaParser.parse(content)
            return JavaParserNode(tree, null)
        } catch (e: ParseProblemException) {
            throw ParsingException(parserType = "JavaParser", language = "Java", exc = e)
        }
    }
}
