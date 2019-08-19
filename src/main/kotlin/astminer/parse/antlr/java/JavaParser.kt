package astminer.parse.antlr.java

import astminer.common.Parser
import astminer.parse.antlr.SimpleNode
import astminer.parse.antlr.convertAntlrTree
import org.antlr.v4.runtime.CommonTokenStream
import me.vovak.antlr.parser.Java8Lexer
import me.vovak.antlr.parser.Java8Parser
import org.antlr.v4.runtime.CharStreams
import java.io.File
import java.io.InputStream

class JavaParser : Parser<SimpleNode> {
    override fun parse(content: InputStream): SimpleNode? {
        val lexer = Java8Lexer(CharStreams.fromStream(content))
        lexer.removeErrorListeners()
        val tokens = CommonTokenStream(lexer)
        val parser = Java8Parser(tokens)
        parser.removeErrorListeners()
        val context = parser.compilationUnit()
        return convertAntlrTree(context, Java8Parser.ruleNames)
    }

    override fun parseProject(projectRoot: File, getFilesToParse: (File) -> List<File>): List<SimpleNode?> {
        return getFilesToParse(projectRoot).map { parse(it.inputStream()) }
    }
}