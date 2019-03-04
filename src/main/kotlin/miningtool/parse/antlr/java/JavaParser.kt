package miningtool.parse.antlr.java

import miningtool.common.Parser
import miningtool.parse.antlr.SimpleNode
import miningtool.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import me.vovak.antlr.parser.Java8Lexer
import me.vovak.antlr.parser.Java8Parser
import java.io.InputStream

class JavaParser : Parser<SimpleNode> {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = Java8Lexer(ANTLRInputStream(content))
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = Java8Parser(tokens)
        parser.removeErrorListeners()
        val context = parser.compilationUnit()
        return convertAntlrTree(context, Java8Parser.ruleNames)
    }

}