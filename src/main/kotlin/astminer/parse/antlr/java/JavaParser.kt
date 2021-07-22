package astminer.parse.antlr.java

import astminer.common.model.Parser
import astminer.parse.ParsingException
import astminer.parse.antlr.AntlrNode
import astminer.parse.antlr.convertAntlrTree
import me.vovak.antlr.parser.Java8Lexer
import me.vovak.antlr.parser.Java8Parser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream
import java.lang.Exception

class JavaParser : Parser<AntlrNode> {
    override fun parseInputStream(content: InputStream): AntlrNode {
        return try {
            val lexer = Java8Lexer(CharStreams.fromStream(content))
            lexer.removeErrorListeners()
            val tokens = CommonTokenStream(lexer)
            val parser = Java8Parser(tokens)
            parser.removeErrorListeners()
            val context = parser.compilationUnit()
            convertAntlrTree(context, Java8Parser.ruleNames, Java8Parser.VOCABULARY)
        } catch (e: Exception) {
            throw ParsingException("ANTLR", "Java", e)
        }
    }
}
