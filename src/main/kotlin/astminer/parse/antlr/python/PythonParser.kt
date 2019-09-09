package astminer.parse.antlr.python

import me.vovak.antlr.parser.Python3Lexer
import me.vovak.antlr.parser.Python3Parser
import astminer.common.Parser
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.io.InputStream

class PythonParser : Parser<SimpleNode> {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = Python3Lexer(CharStreams.fromStream(content))
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = Python3Parser(tokens)
        parser.removeErrorListeners()
        val context = parser.file_input()
        return convertAntlrTree(context, Python3Parser.ruleNames)
    }
}