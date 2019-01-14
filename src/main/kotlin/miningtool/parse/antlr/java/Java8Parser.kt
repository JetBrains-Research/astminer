package miningtool.parse.antlr.java

import me.vovak.antlr.parser.JavaLexer
import me.vovak.antlr.parser.JavaParser
import miningtool.common.Parser
import miningtool.parse.antlr.SimpleNode
import miningtool.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import java.io.InputStream

class Java8Parser : Parser<SimpleNode> {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = JavaLexer(ANTLRInputStream(content))
        val tokens = CommonTokenStream(lexer)
        val parser = JavaParser(tokens)
        val context = parser.compilationUnit()
        return convertAntlrTree(context, JavaParser.ruleNames)
    }

}