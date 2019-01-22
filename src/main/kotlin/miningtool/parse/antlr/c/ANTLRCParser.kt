package miningtool.parse.antlr.c

import me.vovak.antlr.parser.CLexer
import me.vovak.antlr.parser.CParser
import miningtool.common.Parser
import miningtool.parse.antlr.SimpleNode
import miningtool.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ConsoleErrorListener
import java.io.InputStream

class ANTLRCParser: Parser<SimpleNode> {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = CLexer(ANTLRInputStream(content))
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = CParser(tokens)
        parser.removeErrorListeners()
        val context = parser.compilationUnit()
        return convertAntlrTree(context, CParser.ruleNames)
    }
}