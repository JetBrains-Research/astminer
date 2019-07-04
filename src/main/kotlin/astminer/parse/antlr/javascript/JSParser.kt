package astminer.parse.antlr.javascript

import astminer.common.Parser
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.convertAntlrTree
import me.vovak.antlr.parser.JavaScriptLexer
import me.vovak.antlr.parser.JavaScriptParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream

class JSParser : Parser<SimpleNode> {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = JavaScriptLexer(CharStreams.fromStream(content))
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = JavaScriptParser(tokens)
        parser.removeErrorListeners()
        val context = parser.program()
        return convertAntlrTree(context, JavaScriptParser.ruleNames)
    }
}