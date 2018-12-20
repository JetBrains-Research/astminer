package miningtool.parse.antlr.java

import me.vovak.antlr.parser.Java8Lexer
import me.vovak.antlr.parser.Java8Parser
import miningtool.common.Node
import miningtool.common.Parser
import miningtool.parse.antlr.SimpleNode
import miningtool.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream

class JavaParser : Parser<SimpleNode>() {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = Java8Lexer(ANTLRInputStream(content))
        val tokens = CommonTokenStream(lexer)
        val parser = Java8Parser(tokens)

        val context = parser.compilationUnit()

        return convertAntlrTree(context, Java8Parser.ruleNames)
    }

}