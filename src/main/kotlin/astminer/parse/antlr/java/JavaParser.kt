package astminer.parse.antlr.java

import astminer.common.model.Parser
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.CommonTokenStream
import me.vovak.antlr.parser.Java8Lexer
import me.vovak.antlr.parser.Java8Parser
import org.antlr.v4.runtime.CharStreams
import java.io.InputStream
import java.lang.Exception

class JavaParser : Parser<SimpleNode> {
    override fun parseInputStream(content: InputStream): SimpleNode? {
        return try {
            val lexer = Java8Lexer(CharStreams.fromStream(content))
            lexer.removeErrorListeners()
            val tokens = CommonTokenStream(lexer)
            val parser = Java8Parser(tokens)
            parser.removeErrorListeners()
            val context = parser.compilationUnit()
            convertAntlrTree(context, Java8Parser.ruleNames, Java8Parser.VOCABULARY)
        } catch (e: Exception) {
            null
        }
    }
}