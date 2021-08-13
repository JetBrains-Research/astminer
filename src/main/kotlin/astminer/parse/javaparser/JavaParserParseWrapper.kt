package astminer.parse.javaparser

import astminer.common.model.Parser
import astminer.parse.ParsingException
import com.github.javaparser.JavaParser
import com.github.javaparser.ParseProblemException
import mu.KotlinLogging
import java.io.InputStream

private val logger = KotlinLogging.logger("JavaParser")

class JavaParserParseWrapper : Parser<JavaParserNode> {
    override fun parseInputStream(content: InputStream): JavaParserNode {
        try {
            val tree = JavaParser().parse(content).result.get()
            return JavaParserNode(tree, null)
        } catch (e: ParseProblemException) {
            throw ParsingException(parserType = "JavaParser", language = "Java", exc = e)
        } catch (e: NoSuchElementException) {
            throw ParsingException(parserType = "JavaParser", language = "Java", exc = e)
        }
    }
}
