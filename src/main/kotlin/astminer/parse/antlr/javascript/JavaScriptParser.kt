package astminer.parse.antlr.javascript

import astminer.common.model.Parser
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.convertAntlrTree
import me.vovak.antlr.parser.JavaScriptLexer
import me.vovak.antlr.parser.JavaScriptParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream
import java.lang.Exception

class JavaScriptParser : Parser<SimpleNode> {
    override fun parseInputStream(content: InputStream): SimpleNode? {
        return try {
            val lexer = JavaScriptLexer(CharStreams.fromStream(content))
            lexer.removeErrorListeners()
            val tokens = CommonTokenStream(lexer)
            val parser = JavaScriptParser(tokens)
            parser.removeErrorListeners()
            val context = parser.program()
            convertAntlrTree(context, JavaScriptParser.ruleNames, JavaScriptParser.VOCABULARY)
        } catch (e: Exception) {
            null
        }
    }
}